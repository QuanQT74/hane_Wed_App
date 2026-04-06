package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * TierBenefit entity - Ánh xạ tới bảng "tier_benefit".
 * Liên kết nhiều-nhiều giữa hạng hội viên và quyền lợi.
 * @UniqueConstraint đảm bảo mỗi cặp (tier_id, benefit_id) là duy nhất
 */
@Entity
@Table(name = "tier_benefit", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"tier_id", "benefit_id"})
})
public class TierBenefit {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Hạng hội viên.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private MembershipTier tier;

    /**
     * Quyền lợi.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefit_id", nullable = false)
    private Benefit benefit;

    /**
     * Thứ tự hiển thị.
     */
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected TierBenefit() {
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

    public MembershipTier getTier() {
        return tier;
    }

    public void setTier(MembershipTier tier) {
        this.tier = tier;
    }

    public Benefit getBenefit() {
        return benefit;
    }

    public void setBenefit(Benefit benefit) {
        this.benefit = benefit;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "TierBenefit{" +
                "id=" + id +
                ", displayOrder=" + displayOrder +
                '}';
    }
}
