package com.henawedapp.backend.service;

import com.henawedapp.backend.dto.request.LoginRequest;
import com.henawedapp.backend.dto.request.RefreshTokenRequest;
import com.henawedapp.backend.dto.response.AccountInfoResponse;
import com.henawedapp.backend.dto.response.LoginResponse;
import com.henawedapp.backend.exception.AuthenticationException;
import com.henawedapp.backend.exception.ForbiddenAccessException;
import com.henawedapp.backend.model.entity.Account;
import com.henawedapp.backend.model.entity.MemberProfile;
import com.henawedapp.backend.model.enums.AccountStatus;
import com.henawedapp.backend.model.enums.MemberType;
import com.henawedapp.backend.repository.AccountRepository;
import com.henawedapp.backend.repository.MemberProfileRepository;
import com.henawedapp.backend.security.AccountUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service xử lý authentication (login, logout, refresh token).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final AccountRepository accountRepository;
    private final MemberProfileRepository memberProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    /**
     * Xử lý đăng nhập.
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String contactValue = request.getContactValue().trim();
        String password = request.getPassword();

        // Tìm account bằng email hoặc phone
        Account account = accountRepository.findByEmail(contactValue)
                .or(() -> accountRepository.findByPhone(contactValue))
                .orElseThrow(() -> new AuthenticationException("Tài khoản hoặc mật khẩu không đúng."));

        // Kiểm tra password
        if (!passwordEncoder.matches(password, account.getPasswordHash())) {
            log.warn("Login failed: invalid password for {}", contactValue);
            throw new AuthenticationException("Tài khoản hoặc mật khẩu không đúng.");
        }

        // Kiểm tra account status
        validateAccountStatus(account);

        // Tạo tokens
        Set<String> roles = account.getAccountRoleMaps().stream()
                .map(arm -> arm.getRole().getCode())
                .collect(Collectors.toSet());

        String accessToken = jwtService.generateAccessToken(account.getId(), account.getEmail(), roles);
        String refreshToken = jwtService.generateRefreshToken(account.getId(), account.getEmail(), roles);

        log.info("Login successful for accountId: {}", account.getId());

        return buildLoginResponse(account, roles, accessToken, refreshToken);
    }

    /**
     * Xử lý đăng xuất.
     */
    public void logout(String accessToken, String refreshToken) {
        // Tính thời gian còn lại trước khi hết hạn
        long accessExpiration = jwtService.isTokenExpired(accessToken) ? 0 : accessTokenExpiration;
        long refreshExpiration = jwtService.isTokenExpired(refreshToken) ? 0 : refreshTokenExpiration;
        // Blacklist tokens
        if (accessExpiration > 0) {
            tokenBlacklistService.blacklistToken(accessToken, accessExpiration);
        }
        if (refreshExpiration > 0) {
            tokenBlacklistService.blacklistToken(refreshToken, refreshExpiration);
        }

        log.info("Logout successful, tokens blacklisted");
    }

    /**
     * Xử lý refresh token.
     */
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate token
        if (!jwtService.validateToken(refreshToken)) {
            throw new AuthenticationException("Refresh token không hợp lệ hoặc đã hết hạn.");
        }

        // Kiểm tra token type
        String tokenType = jwtService.extractTokenType(refreshToken);
        if (!"refresh".equals(tokenType)) {
            throw new AuthenticationException("Token không phải là refresh token.");
        }

        // Kiểm tra blacklist
        if (tokenBlacklistService.isTokenBlacklisted(refreshToken)) {
            throw new AuthenticationException("Refresh token đã bị vô hiệu hóa.");
        }

        // Lấy thông tin từ token
        var accountId = jwtService.extractAccountId(refreshToken);
        String email = jwtService.extractEmail(refreshToken);
        Set<String> roles = jwtService.extractRoles(refreshToken);

        // Load account để validate
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AuthenticationException("Tài khoản không tồn tại."));

        validateAccountStatus(account);

        // Blacklist old refresh token
        tokenBlacklistService.blacklistToken(refreshToken, refreshTokenExpiration);

        // Tạo tokens mới
        String newAccessToken = jwtService.generateAccessToken(accountId, email, roles);
        String newRefreshToken = jwtService.generateRefreshToken(accountId, email, roles);

        log.info("Token refreshed for accountId: {}", accountId);

        return buildLoginResponse(account, roles, newAccessToken, newRefreshToken);
    }

    /**
     * Lấy thông tin tài khoản hiện tại.
     */
    public AccountInfoResponse getCurrentAccountInfo(AccountUserDetails userDetails) {
        return AccountInfoResponse.builder()
                .accountId(userDetails.getAccountId())
                .email(userDetails.getEmail())
                .phone(userDetails.getPhone())
                .roles(userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toSet()))
                .build();
    }

    // ========== Private Methods ==========

    /**
     * Kiểm tra trạng thái tài khoản.
     */
    private void validateAccountStatus(Account account) {
        // Kiểm tra isActivated
        if (account.getIsActivated() == null || !account.getIsActivated()) {
            log.warn("Login blocked: account not activated - {}", account.getId());
            throw new ForbiddenAccessException(
                    "Tài khoản chưa được kích hoạt. Vui lòng kích hoạt tài khoản trước khi đăng nhập.");
        }

        // Kiểm tra status
        if (account.getStatus() == AccountStatus.SUSPENDED) {
            log.warn("Login blocked: account suspended - {}", account.getId());
            throw new ForbiddenAccessException("Tài khoản đang bị tạm ngưng. Vui lòng liên hệ hỗ trợ.");
        }

        if (account.getStatus() == AccountStatus.BANNED) {
            log.warn("Login blocked: account banned - {}", account.getId());
            throw new ForbiddenAccessException("Tài khoản đã bị cấm. Vui lòng liên hệ hỗ trợ.");
        }

        if (account.getStatus() == AccountStatus.DELETED) {
            log.warn("Login blocked: account deleted - {}", account.getId());
            throw new ForbiddenAccessException("Tài khoản không còn tồn tại.");
        }
    }

    /**
     * Build login response.
     */
    private LoginResponse buildLoginResponse(Account account, Set<String> roles, String accessToken,
            String refreshToken) {
        // Lấy member info nếu có
        LoginResponse.MemberInfo memberInfo = null;
        MemberProfile memberProfile = memberProfileRepository.findByAccountId(account.getId()).orElse(null);

        if (memberProfile != null) {
            String redirectKey = determineRedirectKey(roles, memberProfile);
            memberInfo = LoginResponse.MemberInfo.builder()
                    .memberProfileId(memberProfile.getId())
                    .memberCode(memberProfile.getMemberCode())
                    .memberType(memberProfile.getProfileType())
                    .displayName(memberProfile.getDisplayName())
                    .redirectKey(redirectKey)
                    .build();
        }

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessTokenExpiration / 1000) // Convert to seconds
                .account(LoginResponse.AccountInfo.builder()
                        .accountId(account.getId())
                        .email(account.getEmail())
                        .phone(account.getPhone())
                        .roles(roles)
                        .build())
                .member(memberInfo)
                .build();
    }

    /**
     * Xác định redirect key dựa trên roles và member type.
     */
    private String determineRedirectKey(Set<String> roles, MemberProfile memberProfile) {
        // Admin luôn vào admin dashboard
        if (roles.contains("ADMIN")) {
            return "ADMIN_DASHBOARD";
        }

        // Kiểm tra member profile
        if (memberProfile.getProfileType() == MemberType.INDIVIDUAL) {
            return "MEMBER_DASHBOARD";
        } else if (memberProfile.getProfileType() == MemberType.ORGANIZATION) {
            return "MEMBER_DASHBOARD";
        }

        // Registered user chưa có member profile
        return "PENDING_APPROVAL";
    }
}