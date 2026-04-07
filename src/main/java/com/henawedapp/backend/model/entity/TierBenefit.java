package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * TierBenefit - Liên kết nhiều-nhiều giữa hạng hội viên và quyền lợi.
 */
@Entity
@Table(name = "tier_benefit", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tier_id", "benefit_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"tier", "benefit"})
public class TierBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private Benefit benefit;

    @Column(name = "display_order", nullable = false)
    @Builder.Default
    private Integer displayOrder = 0;
}
