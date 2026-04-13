package com.henawedapp.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO để cập nhật visibility của attachment.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAttachmentVisibilityRequest {

    @NotNull(message = "isPublic không được để trống")
    private Boolean isPublic;
}