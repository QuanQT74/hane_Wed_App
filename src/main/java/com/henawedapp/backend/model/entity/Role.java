package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Role entity - Ánh xạ tới bảng "role".
 * Vai trò người dùng trong hệ thống.
 */
@Entity
@Table(name = "role")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isActive = true;

    // ==================== Inverse Sides (mappedBy) ====================

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    @Builder.Default
    private List<AccountRoleMap> accountRoleMaps = new ArrayList<>();

    @Override
    public String toString() {
        return "Role{id=" + id + ", code='" + code + "', name='" + name + "'}";
    }
}
