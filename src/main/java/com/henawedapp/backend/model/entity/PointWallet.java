package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * PointWallet entity - Ánh xạ tới bảng "point_wallet".
 * Ví điểm của hội viên.
 */
@Entity
@Table(name = "point_wallet")
public class PointWallet {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Hồ sơ hội viên sở hữu ví.
     * @OneToOne thiết lập quan hệ 1-1 với MemberProfile
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", unique = true, nullable = false)
    private MemberProfile memberProfile;

    /**
     * Số dư điểm hiện tại.
     */
    @Column(name = "current_balance", nullable = false)
    private Integer currentBalance = 0;

    /**
     * Số điểm đã sử dụng.
     */
    @Column(name = "used_points", nullable = false)
    private Integer usedPoints = 0;

    /**
     * Số điểm sắp hết hạn.
     */
    @Column(name = "expiring_points", nullable = false)
    private Integer expiringPoints = 0;

    /**
     * Thời gian cập nhật cuối.
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    @PreUpdate
    protected void onPersist() {
        this.updatedAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected PointWallet() {
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

    public MemberProfile getMemberProfile() {
        return memberProfile;
    }

    public void setMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Integer getUsedPoints() {
        return usedPoints;
    }

    public void setUsedPoints(Integer usedPoints) {
        this.usedPoints = usedPoints;
    }

    public Integer getExpiringPoints() {
        return expiringPoints;
    }

    public void setExpiringPoints(Integer expiringPoints) {
        this.expiringPoints = expiringPoints;
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
        return "PointWallet{" +
                "id=" + id +
                ", currentBalance=" + currentBalance +
                ", usedPoints=" + usedPoints +
                ", expiringPoints=" + expiringPoints +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
