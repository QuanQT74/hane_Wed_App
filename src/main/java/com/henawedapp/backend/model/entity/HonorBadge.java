package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.util.UUID;

/**
 * HonorBadge entity - Ánh xạ tới bảng "honor_badge".
 * Danh hiệu/HC vinh danh.
 */
@Entity
@Table(name = "honor_badge")
public class HonorBadge {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Tên danh hiệu.
     */
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    /**
     * Mô tả danh hiệu.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Cờ hiển thị công khai.
     */
    @Column(name = "is_public", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isPublic = true;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected HonorBadge() {
    }

    // ============================================================
    // GETTERS & SETTERS
    // ============================================================

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "HonorBadge{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isPublic=" + isPublic +
                '}';
    }
}
