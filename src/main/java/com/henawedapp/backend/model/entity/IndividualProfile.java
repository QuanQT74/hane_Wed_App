package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * IndividualProfile - Hồ sơ cá nhân.
 * Liên kết 1-1 với MemberProfile.
 */
@Entity
@Table(name = "individual_profile")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "memberProfileId")
@ToString(exclude = "memberProfile")
public class IndividualProfile {

    @Id
    @Column(name = "member_profile_id", columnDefinition = "BINARY(16)")
    private UUID memberProfileId;

    @OneToOne(fetch = FetchType.LAZY)
    @PrimaryKeyJoinColumn(name = "member_profile_id")
    private MemberProfile memberProfile;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "occupation")
    private String occupation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_group_id")
    private IndustryGroup industryGroup;

    @Column(name = "address_text", columnDefinition = "TEXT")
    private String addressText;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.memberProfile != null && this.memberProfile.getId() != null) {
            this.memberProfileId = this.memberProfile.getId();
        }
    }
}
