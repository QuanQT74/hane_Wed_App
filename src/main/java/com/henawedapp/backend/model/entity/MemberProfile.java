package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.MemberType;
import com.henawedapp.backend.model.enums.VisibilityLevel;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MemberProfile - Hồ sơ thành viên.
 * Liên kết 1-1 với Account.
 */
@Entity
@Table(name = "member_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {
    "account", "currentTier", "individualProfile", "organizationProfile",
    "profileAttachments", "digitalNameCard", "tierHistories", "pointWallet",
    "rewardRedemptions", "referralLinks", "eventRegistrations", "certificates", "memberHonors"
})
public class MemberProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", unique = true, nullable = false)
    private Account account;

    @Column(name = "member_code", unique = true, nullable = false, length = 50)
    private String memberCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "profile_type", nullable = false, length = 20)
    private MemberType profileType;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "public_visibility", nullable = false, length = 20)
    @Builder.Default
    private VisibilityLevel publicVisibility = VisibilityLevel.MEMBERS_ONLY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_tier_id")
    private MembershipTier currentTier;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, updatable = false)
    private Instant updatedAt;

    // ========== Relationships ==========

    @OneToOne(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private IndividualProfile individualProfile;

    @OneToOne(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private OrganizationProfile organizationProfile;

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ProfileAttachment> profileAttachments = new ArrayList<>();

    @OneToOne(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private DigitalNameCard digitalNameCard;

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TierHistory> tierHistories = new ArrayList<>();

    @OneToOne(mappedBy = "memberProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private PointWallet pointWallet;

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<RewardRedemption> rewardRedemptions = new ArrayList<>();

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ReferralLink> referralLinks = new ArrayList<>();

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<EventRegistration> eventRegistrations = new ArrayList<>();

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Certificate> certificates = new ArrayList<>();

    @OneToMany(mappedBy = "memberProfile", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MemberHonor> memberHonors = new ArrayList<>();

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }
}
