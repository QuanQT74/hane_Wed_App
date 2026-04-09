package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.MemberType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * IndividualProfileData - Lưu tạm thông tin hồ sơ cá nhân trong quá trình đăng ký.
 * Entity này chỉ tồn tại trong giai đoạn Pending Registration, và sẽ được copy sang
 * IndividualProfile thực sự khi OTP xác thực thành công.
 */
@Entity
@Table(name = "individual_profile_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class IndividualProfileData {

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
     * Họ tên đầy đủ.
     */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /**
     * Nghề nghiệp.
     */
    @Column(name = "occupation", length = 255)
    private String occupation;

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
