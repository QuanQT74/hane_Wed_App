package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * MemberHonor - Danh hiệu được trao cho hội viên.
 */
@Entity
@Table(name = "member_honor", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"member_profile_id", "honor_badge_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"memberProfile", "honorBadge", "grantedBy"})
public class MemberHonor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "honor_badge_id", nullable = false)
    private HonorBadge honorBadge;

    @Column(name = "granted_at", nullable = false)
    private Instant grantedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by")
    private Account grantedBy;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.grantedAt == null) {
            this.grantedAt = Instant.now();
        }
    }
}
