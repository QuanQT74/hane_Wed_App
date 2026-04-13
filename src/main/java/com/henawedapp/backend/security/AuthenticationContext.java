package com.henawedapp.backend.security;

import com.henawedapp.backend.exception.ForbiddenAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Helper để lấy current authenticated user từ Spring Security context.
 */
@Component
public class AuthenticationContext {

    /**
     * Lấy current account ID từ SecurityContext.
     *
     * @return UUID của account đang authenticated
     * @throws ForbiddenAccessException nếu không có authentication
     */
    public UUID getCurrentAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenAccessException("Yêu cầu xác thực. Vui lòng đăng nhập.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AccountUserDetails userDetails) {
            return userDetails.getAccountId();
        }

        throw new ForbiddenAccessException("Không thể xác định người dùng hiện tại.");
    }

    /**
     * Lấy current AccountUserDetails từ SecurityContext.
     *
     * @return AccountUserDetails của user hiện tại
     * @throws ForbiddenAccessException nếu không có authentication
     */
    public AccountUserDetails getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ForbiddenAccessException("Yêu cầu xác thực. Vui lòng đăng nhập.");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AccountUserDetails userDetails) {
            return userDetails;
        }

        throw new ForbiddenAccessException("Không thể xác định người dùng hiện tại.");
    }
}
