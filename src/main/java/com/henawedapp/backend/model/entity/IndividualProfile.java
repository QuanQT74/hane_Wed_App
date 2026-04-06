package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * IndividualProfile entity - Ánh xạ tới bảng "individual_profile".
 * Hồ sơ cá nhân, liên kết 1-1 với MemberProfile.
 * @PrimaryKeyJoinColumn thiết lập khóa chính của bảng này cũng là khóa ngoại
 */
@Entity
@Table(name = "individual_profile")
public class IndividualProfile {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @Column(name = "member_profile_id", columnDefinition = "BINARY(16)")
    private UUID memberProfileId;

    /**
     * Liên kết 1-1 với MemberProfile.
     * @OneToOne thiết lập quan hệ 1-1
     * @PrimaryKeyJoinColumn khóa chính cũng là khóa ngoại
     */
    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "member_profile_id")
    private MemberProfile memberProfile;

    /**
     * Họ và tên đầy đủ.
     */
    @Column(name = "full_name", nullable = false)
    private String fullName;

    /**
     * Nghề nghiệp.
     */
    @Column(name = "occupation")
    private String occupation;

    /**
     * Nhóm ngành.
     */
    @Column(name = "industry_group_id", columnDefinition = "BINARY(16)")
    private UUID industryGroupId;

    /**
     * Địa chỉ dạng text.
     */
    @Column(name = "address_text", columnDefinition = "TEXT")
    private String addressText;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected IndividualProfile() {
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public UUID getMemberProfileId() {
        return memberProfileId;
    }

    public void setMemberProfileId(UUID memberProfileId) {
        this.memberProfileId = memberProfileId;
    }

    public MemberProfile getMemberProfile() {
        return memberProfile;
    }

    public void setMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public UUID getIndustryGroupId() {
        return industryGroupId;
    }

    public void setIndustryGroupId(UUID industryGroupId) {
        this.industryGroupId = industryGroupId;
    }

    public String getAddressText() {
        return addressText;
    }

    public void setAddressText(String addressText) {
        this.addressText = addressText;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "IndividualProfile{" +
                "memberProfileId=" + memberProfileId +
                ", fullName='" + fullName + '\'' +
                ", occupation='" + occupation + '\'' +
                ", industryGroupId=" + industryGroupId +
                ", addressText='" + addressText + '\'' +
                '}';
    }
}
