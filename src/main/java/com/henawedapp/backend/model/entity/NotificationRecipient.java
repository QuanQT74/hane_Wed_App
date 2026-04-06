package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * NotificationRecipient entity - Ánh xạ tới bảng "notification_recipient".
 * Người nhận thông báo.
 */
@Entity
@Table(name = "notification_recipient")
public class NotificationRecipient {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum trạng thái gửi.
     */
    public enum DeliveryStatus {
        PENDING,
        SENT,
        FAILED
    }

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Thông báo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    /**
     * Tài khoản nhận thông báo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    /**
     * Trạng thái gửi.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status", nullable = false, length = 50)
    private DeliveryStatus deliveryStatus = DeliveryStatus.PENDING;

    /**
     * Thời gian đọc.
     */
    @Column(name = "read_at")
    private Instant readAt;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected NotificationRecipient() {
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

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(DeliveryStatus deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "NotificationRecipient{" +
                "id=" + id +
                ", deliveryStatus=" + deliveryStatus +
                ", readAt=" + readAt +
                '}';
    }
}
