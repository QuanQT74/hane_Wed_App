package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Account - Tài khoản người dùng.
 * Là entity chính cho authentication và authorization.
 */
@Entity
@Table(name = "account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {
    "accountRoleMaps", "emailVerificationToken", "passwordResetTokens",
    "membershipApplications", "memberProfile", "notificationRecipients", "auditLogs"
})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    
    @Column(name = "phone", unique = true, length = 20)
    private String phone;


    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private AccountStatus status = AccountStatus.PENDING;

    @Column(name = "is_activated", nullable = false )
    @Builder.Default
    private Boolean isActivated = false;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at",nullable = false, updatable = false)
    private Instant updatedAt;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AccountRoleMap> accountRoleMaps = new ArrayList<>();

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private EmailVerificationToken emailVerificationToken;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PasswordResetToken> passwordResetTokens = new ArrayList<>();

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @Builder.Default
    private List<MembershipApplication> membershipApplications = new ArrayList<>();

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private MemberProfile memberProfile;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<NotificationRecipient> notificationRecipients = new ArrayList<>();

    @OneToMany(mappedBy = "actorAccount", cascade = CascadeType.ALL)
    @Builder.Default
    private List<AuditLog> auditLogs = new ArrayList<>();

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            setCreatedAt(now);
        }
        if (this.updatedAt == null) {
            setUpdatedAt(now);
        }
    }
}
