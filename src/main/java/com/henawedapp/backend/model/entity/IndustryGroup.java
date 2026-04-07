package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * IndustryGroup entity - Ánh xạ tới bảng "industry_group".
 * Nhóm ngành nghề.
 */
@Entity
@Table(name = "industry_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndustryGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isActive = true;

    // ==================== Inverse Sides (mappedBy) ====================

    @OneToMany(mappedBy = "industryGroup", fetch = FetchType.LAZY)
    @Builder.Default
    private List<IndividualProfile> individualProfiles = new ArrayList<>();

    @OneToMany(mappedBy = "industryGroup", fetch = FetchType.LAZY)
    @Builder.Default
    private List<OrganizationProfile> organizationProfiles = new ArrayList<>();

    @Override
    public String toString() {
        return "IndustryGroup{id=" + id + ", code='" + code + "', name='" + name + "'}";
    }
}
