package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.NameCardPlan;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * DigitalNameCard - Danh thiếp cá nhân số.
 * Mỗi thành viên chỉ có một danh thiếp.
 */
@Entity
@Table(name = "digital_name_card")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "memberProfile")
public class DigitalNameCard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", unique = true, nullable = false)
    private MemberProfile memberProfile;

    @Column(name = "share_code", unique = true, nullable = false, length = 100)
    private String shareCode;

    @Column(name = "share_url", nullable = false, columnDefinition = "TEXT")
    private String shareUrl;

    @Column(name = "qr_code_url", columnDefinition = "TEXT")
    private String qrCodeUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false, length = 20)
    @Builder.Default
    private NameCardPlan planType = NameCardPlan.BASIC;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) this.createdAt = now;
        if (this.updatedAt == null) this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
