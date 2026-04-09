package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * OrganizationProfileData - Lưu tạm thông tin hồ sơ tổ chức trong quá trình đăng ký.
 * Entity này chỉ tồn tại trong giai đoạn Pending Registration, và sẽ được copy sang
 * OrganizationProfile thực sự khi OTP xác thực thành công.
 */
@Entity
@Table(name = "organization_profile_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class OrganizationProfileData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * PendingRegistration liên kết với dữ liệu này.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pending_registration_id", nullable = false, unique = true)
    private PendingRegistration pendingRegistration;

    /**
     * Tên tổ chức.
     */
    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    /**
     * Người đại diện theo pháp luật.
     */
    @Column(name = "legal_representative", length = 255)
    private String legalRepresentative;

    /**
     * Website của tổ chức.
     */
    @Column(name = "website", length = 255)
    private String website;

    /**
     * Tóm tắt sản phẩm/dịch vụ.
     */
    @Column(name = "product_service_summary", columnDefinition = "TEXT")
    private String productServiceSummary;

    /**
     * Địa chỉ.
     */
    @Column(name = "address_text", columnDefinition = "TEXT")
    private String addressText;

    /**
     * Mã nhóm ngành (nếu có).
     */
    @Column(name = "industry_group_code", length = 50)
    private String industryGroupCode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
