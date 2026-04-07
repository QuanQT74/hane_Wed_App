package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.RewardStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Reward - Phần thưởng có thể đổi bằng điểm.
 */
@Entity
@Table(name = "reward")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "rewardRedemptions")
public class Reward {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "required_points", nullable = false)
    private Integer requiredPoints;

    @Column(name = "stock_qty", nullable = false)
    @Builder.Default
    private Integer stockQty = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private RewardStatus status = RewardStatus.ACTIVE;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "reward")
    @Builder.Default
    private List<RewardRedemption> rewardRedemptions = new ArrayList<>();
}
