package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * CertificateType - Loại chứng chỉ.
 */
@Entity
@Table(name = "certificate_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = "certificates")
public class CertificateType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "certificateType")
    @Builder.Default
    private List<Certificate> certificates = new ArrayList<>();
}
