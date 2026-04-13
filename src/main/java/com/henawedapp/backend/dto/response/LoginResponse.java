package com.henawedapp.backend.dto.response;

import com.henawedapp.backend.model.enums.MemberType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Response DTO sau khi login thành công.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    /**
     * Access token để truy cập API.
     */
    private String accessToken;

    /**
     * Refresh token để lấy access token mới.
     */
    private String refreshToken;

    /**
     * Loại token.
     */
    private String tokenType;

    /**
     * Thời gian hết hạn của access token (giây).
     */
    private Long expiresIn;

    /**
     * Thông tin tài khoản.
     */
    private AccountInfo account;

    /**
     * Thông tin hồ sơ thành viên (nếu có).
     */
    private MemberInfo member;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AccountInfo {
        private UUID accountId;
        private String email;
        private String phone;
        private Set<String> roles;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MemberInfo {
        private UUID memberProfileId;
        private String memberCode;
        private MemberType memberType;
        private String displayName;
        /**
         * Key để frontend điều hướng:
         * - "ADMIN_DASHBOARD" → Admin dashboard
         * - "MEMBER_DASHBOARD" → Member dashboard
         * - "PENDING_APPROVAL" → Chờ phê duyệt
         */
        private String redirectKey;
    }
}
