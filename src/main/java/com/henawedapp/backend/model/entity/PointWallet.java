package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * PointWallet - Ví điểm của hội viên.
 */
@Entity
@Table(name = "point_wallet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"memberProfile", "pointTransactions"})
public class PointWallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", unique = true, nullable = false)
    private MemberProfile memberProfile;

    @Column(name = "current_balance", nullable = false)
    @Builder.Default
    private Integer currentBalance = 0;

    @Column(name = "used_points", nullable = false)
    @Builder.Default
    private Integer usedPoints = 0;

    @Column(name = "expiring_points", nullable = false)
    @Builder.Default
    private Integer expiringPoints = 0;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "wallet", cascade = CascadeType.ALL)
    @Builder.Default
    private List<PointTransaction> pointTransactions = new ArrayList<>();

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    @PreUpdate
    protected void onPersist() {
        this.updatedAt = Instant.now();
    }
}
