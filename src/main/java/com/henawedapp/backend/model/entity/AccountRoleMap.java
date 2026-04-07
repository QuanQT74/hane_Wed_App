package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * AccountRoleMap entity - Ánh xạ tới bảng "account_role_map".
 * Liên kết nhiều-nhiều giữa account và role.
 */
@Entity
@Table(name = "account_role_map")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRoleMap {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @PrePersist
    protected void onCreate() {
        if (this.assignedAt == null) this.assignedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "AccountRoleMap{accountId=" + account.getId() + ", roleId=" + role.getId() + "}";
    }
}
