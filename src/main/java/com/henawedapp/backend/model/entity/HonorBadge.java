package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * HonorBadge - Danh hiệu/HC vinh danh.
 */
@Entity
@Table(name = "honor_badge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "memberHonors")
public class HonorBadge {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_public", nullable = false)
    @Builder.Default
    private Boolean isPublic = true;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "honorBadge")
    @Builder.Default
    private List<MemberHonor> memberHonors = new ArrayList<>();
}
