package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MembershipTier - Hạng hội viên (VD: Bronze, Silver, Gold).
 */
@Entity
@Table(name = "membership_tier")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"tierBenefits", "memberProfiles", "tierHistoriesFrom", "tierHistoriesTo"})
public class MembershipTier {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "tier", cascade = CascadeType.ALL)
    @Builder.Default
    private List<TierBenefit> tierBenefits = new ArrayList<>();

    @OneToMany(mappedBy = "currentTier")
    @Builder.Default
    private List<MemberProfile> memberProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "fromTier")
    @Builder.Default
    private List<TierHistory> tierHistoriesFrom = new ArrayList<>();

    @OneToMany(mappedBy = "toTier")
    @Builder.Default
    private List<TierHistory> tierHistoriesTo = new ArrayList<>();
}
