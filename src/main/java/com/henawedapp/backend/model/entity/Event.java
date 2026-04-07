package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.EventStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Event - Sự kiện trong hệ thống.
 */
@Entity
@Table(name = "event")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"eventType", "eventRegistrations"})
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id")
    private EventType eventType;

    @Column(name = "location_text", columnDefinition = "TEXT")
    private String locationText;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(name = "max_capacity")
    private Integer maxCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private EventStatus status = EventStatus.DRAFT;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    @Builder.Default
    private List<EventRegistration> eventRegistrations = new ArrayList<>();
}
