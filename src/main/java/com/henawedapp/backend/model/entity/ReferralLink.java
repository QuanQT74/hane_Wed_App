package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ReferralLink - Link giới thiệu của hội viên.
 */
@Entity
@Table(name = "referral_link")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"memberProfile", "referralTrackings"})
public class ReferralLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    @Column(name = "referral_code", unique = true, nullable = false, length = 50)
    private String referralCode;

    @Column(name = "share_url", nullable = false, columnDefinition = "TEXT")
    private String shareUrl;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "referralLink", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ReferralTracking> referralTrackings = new ArrayList<>();

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
