package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Role - Vai trò người dùng trong hệ thống.
 * Ví dụ: ADMIN, MEMBER, MODERATOR
 */
@Entity
@Table(name = "role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "accountRoleMaps")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @Builder.Default
    private List<AccountRoleMap> accountRoleMaps = new ArrayList<>();
}
