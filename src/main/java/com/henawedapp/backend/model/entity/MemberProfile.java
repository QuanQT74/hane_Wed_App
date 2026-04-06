package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * MemberProfile entity - Ánh xạ tới bảng "member_profile".
 * Thông tin hồ sơ hội viên chính.
 */
@Entity
@Table(name = "member_profile")
public class MemberProfile {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum loại hồ sơ.
     */
    public enum ProfileType {
        INDIVIDUAL,
        ORGANIZATION
    }

    /**
     * Enum mức độ hiển thị công khai.
     */
    public enum PublicVisibility {
        PUBLIC,
        PRIVATE
    }

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Liên kết 1-1 với Account.
     * @OneToOne thiết lập quan hệ 1-1 với Account
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", unique = true, nullable = false)
    private Account account;

    /**
     * Mã hội viên duy nhất.
     */
    @Column(name = "member_code", unique = true, nullable = false, length = 50)
    private String memberCode;

    /**
     * Loại hồ sơ: INDIVIDUAL hoặc ORGANIZATION.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "profile_type", nullable = false, length = 50)
    private ProfileType profileType;

    /**
     * Tên hiển thị.
     */
    @Column(name = "display_name", nullable = false)
    private String displayName;

    /**
     * URL avatar.
     */
    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    /**
     * Email liên hệ.
     */
    @Column(name = "contact_email")
    private String contactEmail;

    /**
     * Số điện thoại liên hệ.
     */
    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    /**
     * Tiểu sử.
     */
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    /**
     * Mức độ hiển thị công khai, mặc định PUBLIC.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "public_visibility", nullable = false)
    private PublicVisibility publicVisibility = PublicVisibility.PUBLIC;

    /**
     * Hạng hội viên hiện tại.
     * @ManyToOne thiết lập quan hệ nhiều-1 với MembershipTier
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_tier_id")
    private MembershipTier currentTier;

    /**
     * Trạng thái kích hoạt.
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isActive = true;

    /**
     * Thời gian phê duyệt.
     */
    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected MemberProfile() {
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public ProfileType getProfileType() {
        return profileType;
    }

    public void setProfileType(ProfileType profileType) {
        this.profileType = profileType;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public PublicVisibility getPublicVisibility() {
        return publicVisibility;
    }

    public void setPublicVisibility(PublicVisibility publicVisibility) {
        this.publicVisibility = publicVisibility;
    }

    public MembershipTier getCurrentTier() {
        return currentTier;
    }

    public void setCurrentTier(MembershipTier currentTier) {
        this.currentTier = currentTier;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Instant getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "MemberProfile{" +
                "id=" + id +
                ", memberCode='" + memberCode + '\'' +
                ", profileType=" + profileType +
                ", displayName='" + displayName + '\'' +
                ", publicVisibility=" + publicVisibility +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
