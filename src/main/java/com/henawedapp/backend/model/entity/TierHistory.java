package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * TierHistory - Lịch sử thay đổi hạng hội viên.
 */
@Entity
@Table(name = "tier_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"memberProfile", "fromTier", "toTier", "changedBy"})
public class TierHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_tier_id")
    private MembershipTier fromTier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_tier_id", nullable = false)
    private MembershipTier toTier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    private Account changedBy;

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.changedAt == null) {
            this.changedAt = Instant.now();
        }
    }
}
