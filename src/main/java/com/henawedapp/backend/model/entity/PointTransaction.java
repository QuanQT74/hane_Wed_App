package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.PointDirection;
import com.henawedapp.backend.model.enums.PointSourceType;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * PointTransaction - Giao dịch điểm của hội viên.
 */
@Entity
@Table(name = "point_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"wallet", "memberProfile"})
public class PointTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private PointWallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 20)
    private PointDirection direction;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 30)
    private PointSourceType sourceType;

    @Column(name = "source_id", columnDefinition = "BINARY(16)")
    private UUID sourceId;

    @Column(name = "points_delta", nullable = false)
    private Integer pointsDelta;

    @Column(name = "note", columnDefinition = "TEXT")
    private String note;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.occurredAt == null) {
            this.occurredAt = Instant.now();
        }
    }
}
