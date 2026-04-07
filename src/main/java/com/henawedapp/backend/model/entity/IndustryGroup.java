package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * IndustryGroup - Nhóm ngành nghề.
 * Ví dụ: Công nghệ thông tin, Y tế, Giáo dục...
 */
@Entity
@Table(name = "industry_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"individualProfiles", "organizationProfiles"})
public class IndustryGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "industryGroup")
    @Builder.Default
    private List<IndividualProfile> individualProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "industryGroup")
    @Builder.Default
    private List<OrganizationProfile> organizationProfiles = new ArrayList<>();
}
