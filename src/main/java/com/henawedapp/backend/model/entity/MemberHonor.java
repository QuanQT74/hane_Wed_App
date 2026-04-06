package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * MemberHonor entity - Ánh xạ tới bảng "member_honor".
 * Danh hiệu được trao cho hội viên.
 * @UniqueConstraint đảm bảo mỗi hội viên chỉ có 1 danh hiệu mỗi loại
 */
@Entity
@Table(name = "member_honor", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"member_profile_id", "honor_badge_id"})
})
public class MemberHonor {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Hồ sơ hội viên được trao danh hiệu.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    /**
     * Danh hiệu được trao.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "honor_badge_id", nullable = false)
    private HonorBadge honorBadge;

    /**
     * Thời gian trao danh hiệu.
     */
    @Column(name = "granted_at", nullable = false)
    private Instant grantedAt;

    /**
     * Người trao danh hiệu.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "granted_by")
    private Account grantedBy;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.grantedAt == null) this.grantedAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected MemberHonor() {
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

    public HonorBadge getHonorBadge() {
        return honorBadge;
    }

    public void setHonorBadge(HonorBadge honorBadge) {
        this.honorBadge = honorBadge;
    }

    public Instant getGrantedAt() {
        return grantedAt;
    }

    public void setGrantedAt(Instant grantedAt) {
        this.grantedAt = grantedAt;
    }

    public Account getGrantedBy() {
        return grantedBy;
    }

    public void setGrantedBy(Account grantedBy) {
        this.grantedBy = grantedBy;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "MemberHonor{" +
                "id=" + id +
                ", grantedAt=" + grantedAt +
                ", grantedBy=" + (grantedBy != null ? grantedBy.getId() : null) +
                '}';
    }
}
