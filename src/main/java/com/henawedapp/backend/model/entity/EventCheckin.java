package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * EventCheckin - Checkin tham gia sự kiện.
 */
@Entity
@Table(name = "event_checkin")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"registration", "checkedInBy"})
public class EventCheckin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", unique = true, nullable = false)
    private EventRegistration registration;

    @Column(name = "checked_in_at", nullable = false)
    private Instant checkedInAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_in_by")
    private Account checkedInBy;

    @Column(name = "is_valid", nullable = false)
    @Builder.Default
    private Boolean isValid = true;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.checkedInAt == null) {
            this.checkedInAt = Instant.now();
        }
    }
}
