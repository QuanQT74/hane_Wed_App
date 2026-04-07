package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * EventType entity - Ánh xạ tới bảng "event_type".
 * Loại sự kiện.
 */
@Entity
@Table(name = "event_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventType {

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

    @OneToMany(mappedBy = "eventType", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Event> events = new ArrayList<>();

    @Override
    public String toString() {
        return "EventType{id=" + id + ", code='" + code + "', name='" + name + "'}";
    }
}
