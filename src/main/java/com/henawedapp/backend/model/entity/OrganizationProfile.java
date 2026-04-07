package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * OrganizationProfile - Hồ sơ tổ chức.
 * Liên kết 1-1 với MemberProfile.
 */
@Entity
@Table(name = "organization_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "memberProfileId")
@ToString(exclude = "memberProfile")
public class OrganizationProfile {

    @Id
    @Column(name = "member_profile_id", columnDefinition = "BINARY(16)")
    private UUID memberProfileId;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "member_profile_id")
    private MemberProfile memberProfile;

    @Column(name = "organization_name", nullable = false)
    private String organizationName;

    @Column(name = "legal_representative")
    private String legalRepresentative;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_group_id")
    private IndustryGroup industryGroup;

    @Column(name = "website", columnDefinition = "TEXT")
    private String website;

    @Column(name = "product_service_summary", columnDefinition = "TEXT")
    private String productServiceSummary;

    @Column(name = "address_text", columnDefinition = "TEXT")
    private String addressText;
}
