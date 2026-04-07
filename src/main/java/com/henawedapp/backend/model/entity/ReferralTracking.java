package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.ReferralStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * ReferralTracking - Theo dõi lịch sử giới thiệu.
 */
@Entity
@Table(name = "referral_tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"referralLink", "referrerMember", "referredAccount"})
public class ReferralTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referral_link_id")
    private ReferralLink referralLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referrer_member_id")
    private MemberProfile referrerMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "referred_account_id")
    private Account referredAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ReferralStatus status = ReferralStatus.CLICKED;

    @Column(name = "clicked_at", nullable = false)
    private Instant clickedAt;

    @Column(name = "registered_at")
    private Instant registeredAt;

    @Column(name = "qualified_at")
    private Instant qualifiedAt;

    @Column(name = "rewarded_at")
    private Instant rewardedAt;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.clickedAt == null) {
            this.clickedAt = Instant.now();
        }
    }
}
