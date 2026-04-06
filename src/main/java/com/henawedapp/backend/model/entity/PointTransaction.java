package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * PointTransaction entity - Ánh xạ tới bảng "point_transaction".
 * Giao dịch điểm của hội viên.
 */
@Entity
@Table(name = "point_transaction")
public class PointTransaction {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum hướng giao dịch điểm.
     */
    public enum Direction {
        EARN,
        SPEND
    }

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Ví điểm liên quan.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private PointWallet wallet;

    /**
     * Hồ sơ hội viên.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    /**
     * Hướng giao dịch: EARN (kiếm) hoặc SPEND (tiêu).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 20)
    private Direction direction;

    /**
     * Loại nguồn phát sinh.
     */
    @Column(name = "source_type", length = 100)
    private String sourceType;

    /**
     * ID của nguồn phát sinh.
     */
    @Column(name = "source_id", columnDefinition = "BINARY(16)")
    private UUID sourceId;

    /**
     * Số điểm thay đổi.
     */
    @Column(name = "points_delta", nullable = false)
    private Integer pointsDelta;

    /**
     * Ghi chú.
     */
    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    /**
     * Thời gian phát sinh.
     */
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.occurredAt == null) this.occurredAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected PointTransaction() {
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

    public PointWallet getWallet() {
        return wallet;
    }

    public void setWallet(PointWallet wallet) {
        this.wallet = wallet;
    }

    public MemberProfile getMemberProfile() {
        return memberProfile;
    }

    public void setMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public UUID getSourceId() {
        return sourceId;
    }

    public void setSourceId(UUID sourceId) {
        this.sourceId = sourceId;
    }

    public Integer getPointsDelta() {
        return pointsDelta;
    }

    public void setPointsDelta(Integer pointsDelta) {
        this.pointsDelta = pointsDelta;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(Instant occurredAt) {
        this.occurredAt = occurredAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "PointTransaction{" +
                "id=" + id +
                ", direction=" + direction +
                ", sourceType='" + sourceType + '\'' +
                ", pointsDelta=" + pointsDelta +
                ", note='" + note + '\'' +
                ", occurredAt=" + occurredAt +
                '}';
    }
}
