package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * OrganizationProfile entity - Ánh xạ tới bảng "organization_profile".
 * Hồ sơ tổ chức, liên kết 1-1 với MemberProfile.
 */
@Entity
@Table(name = "organization_profile")
public class OrganizationProfile {

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
     * Tên tổ chức.
     */
    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    /**
     * Người đại diện pháp lý.
     */
    @Column(name = "legal_representative")
    private String legalRepresentative;

    /**
     * Nhóm ngành.
     */
    @Column(name = "industry_group_id", columnDefinition = "BINARY(16)")
    private UUID industryGroupId;

    /**
     * Website.
     */
    @Column(name = "website", columnDefinition = "TEXT")
    private String website;

    /**
     * Tóm tắt sản phẩm/dịch vụ.
     */
    @Column(name = "product_service_summary", columnDefinition = "TEXT")
    private String productServiceSummary;

    /**
     * Địa chỉ dạng text.
     */
    @Column(name = "address_text", columnDefinition = "TEXT")
    private String addressText;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected OrganizationProfile() {
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

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getLegalRepresentative() {
        return legalRepresentative;
    }

    public void setLegalRepresentative(String legalRepresentative) {
        this.legalRepresentative = legalRepresentative;
    }

    public UUID getIndustryGroupId() {
        return industryGroupId;
    }

    public void setIndustryGroupId(UUID industryGroupId) {
        this.industryGroupId = industryGroupId;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getProductServiceSummary() {
        return productServiceSummary;
    }

    public void setProductServiceSummary(String productServiceSummary) {
        this.productServiceSummary = productServiceSummary;
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
        return "OrganizationProfile{" +
                "memberProfileId=" + memberProfileId +
                ", organizationName='" + organizationName + '\'' +
                ", legalRepresentative='" + legalRepresentative + '\'' +
                ", industryGroupId=" + industryGroupId +
                ", website='" + website + '\'' +
                '}';
    }
}
