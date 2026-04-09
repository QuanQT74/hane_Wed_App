package com.henawedapp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO phản hồi đăng ký thành công.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponse {

    private UUID accountId;
    private UUID applicationId;
    private String email;
    private String memberType;
    private String status;
    private String message;
    private Instant submittedAt;
    
}
