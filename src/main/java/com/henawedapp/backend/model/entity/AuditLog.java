package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * AuditLog - Nhật ký kiểm tra hệ thống.
 */
@Entity
@Table(name = "audit_log")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "actorAccount")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_account_id")
    private Account actorAccount;

    @Column(name = "action", nullable = false, length = 100)
    private String action;

    @Column(name = "target_type", nullable = false, length = 100)
    private String targetType;

    @Column(name = "target_id", columnDefinition = "BINARY(16)")
    private UUID targetId;

    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

    @Column(name = "metadata_json", columnDefinition = "JSON")
    private String metadataJson;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.loggedAt == null) {
            this.loggedAt = Instant.now();
        }
    }
}
