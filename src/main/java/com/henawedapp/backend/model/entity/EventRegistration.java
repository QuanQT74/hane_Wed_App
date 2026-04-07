package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * EventRegistration - Đăng ký tham gia sự kiện.
 */
@Entity
@Table(name = "event_registration", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "member_profile_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"event", "memberProfile", "eventCheckin"})
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false, length = 20)
    @Builder.Default
    private RegistrationStatus registrationStatus = RegistrationStatus.REGISTERED;

    @Column(name = "qr_code_value", unique = true, length = 255)
    private String qrCodeValue;

    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    // ========== Relationships ==========

    @OneToOne(mappedBy = "registration", cascade = CascadeType.ALL, orphanRemoval = true)
    private EventCheckin eventCheckin;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.registeredAt == null) {
            this.registeredAt = Instant.now();
        }
    }
}
