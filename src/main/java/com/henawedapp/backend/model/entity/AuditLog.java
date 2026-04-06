package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * AuditLog entity - Ánh xạ tới bảng "audit_log".
 * Nhật ký kiểm tra hệ thống.
 */
@Entity
@Data
@Table(name = "audit_log")
public class AuditLog {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Tài khoản thực hiện hành động.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_account_id", nullable = false)
    private Account actorAccount;

    /**
     * Hành động thực hiện.
     */
    @Column(name = "action", nullable = false, length = 100)
    private String action;

    /**
     * Loại đối tượng bị tác động.
     */
    @Column(name = "target_type", nullable = false, length = 100)
    private String targetType;

    /**
     * ID đối tượng bị tác động.
     */
    @Column(name = "target_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID targetId;

    /**
     * Thời gian ghi log.
     */
    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

    /**
     * Dữ liệu bổ sung dạng JSON.
     */
    @Column(name = "metadata_json", columnDefinition = "JSON")
    private String metadataJson;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.loggedAt == null) this.loggedAt = Instant.now();
    }
    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "AuditLog{" +
                "id=" + id +
                ", action='" + action + '\'' +
                ", targetType='" + targetType + '\'' +
                ", targetId=" + targetId +
                ", loggedAt=" + loggedAt +
                '}';
    }
}
