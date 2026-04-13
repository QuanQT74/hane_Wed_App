package com.henawedapp.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

/**
 * Response DTO cho /me endpoint.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountInfoResponse {

    private UUID accountId;
    private String email;
    private String phone;
    private Set<String> roles;
}