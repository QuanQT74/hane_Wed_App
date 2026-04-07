package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.DeliveryStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

/**
 * NotificationRecipient - Người nhận thông báo.
 */
@Entity
@Table(name = "notification_recipient")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"notification", "account"})
public class NotificationRecipient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 20)
    @Builder.Default
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    @Column(name = "read_at")
    private Instant readAt;
}
