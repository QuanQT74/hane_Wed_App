package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.RedemptionStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * RewardRedemption - Lịch sử đổi quà của hội viên.
 */
@Entity
@Table(name = "reward_redemption")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"memberProfile", "reward", "approvedBy"})
public class RewardRedemption {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reward_id")
    private Reward reward;

    @Column(name = "required_points", nullable = false)
    private Integer requiredPoints;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private RedemptionStatus status = RedemptionStatus.PENDING;

    @Column(name = "redeemed_at", nullable = false)
    private Instant redeemedAt;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Account approvedBy;

    @Column(name = "fulfilled_at")
    private Instant fulfilledAt;

    @Column(name = "rejected_reason", columnDefinition = "TEXT")
    private String rejectedReason;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.redeemedAt == null) {
            this.redeemedAt = Instant.now();
        }
    }
}
