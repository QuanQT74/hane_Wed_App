package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

/**
 * CertificateType entity - Ánh xạ tới bảng "certificate_type".
 * Loại chứng chỉ.
 */
@Entity
@Data
@Table(name = "certificate_type")
public class CertificateType {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Tên loại chứng chỉ.
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Trạng thái kích hoạt.
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isActive = true;



    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "CertificateType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
