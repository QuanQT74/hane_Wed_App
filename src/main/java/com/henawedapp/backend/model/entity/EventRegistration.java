package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * EventRegistration entity - Ánh xạ tới bảng "event_registration".
 * Đăng ký tham gia sự kiện.
 */
@Entity
@Table(name = "event_registration")
public class EventRegistration {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum trạng thái đăng ký.
     */
    public enum RegistrationStatus {
        REGISTERED,
        ATTENDED,
        CANCELLED
    }

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Sự kiện đăng ký.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * Hồ sơ hội viên đăng ký.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    /**
     * Trạng thái đăng ký.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "registration_status", nullable = false, length = 50)
    private RegistrationStatus registrationStatus = RegistrationStatus.REGISTERED;

    /**
     * Giá trị QR code.
     */
    @Column(name = "qr_code_value", unique = true)
    private String qrCodeValue;

    /**
     * Thời gian đăng ký.
     */
    @Column(name = "registered_at", nullable = false)
    private Instant registeredAt;

    /**
     * Thời gian hủy.
     */
    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.registeredAt == null) this.registeredAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected EventRegistration() {
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

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public MemberProfile getMemberProfile() {
        return memberProfile;
    }

    public void setMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    public RegistrationStatus getRegistrationStatus() {
        return registrationStatus;
    }

    public void setRegistrationStatus(RegistrationStatus registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getQrCodeValue() {
        return qrCodeValue;
    }

    public void setQrCodeValue(String qrCodeValue) {
        this.qrCodeValue = qrCodeValue;
    }

    public Instant getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(Instant registeredAt) {
        this.registeredAt = registeredAt;
    }

    public Instant getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(Instant cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "EventRegistration{" +
                "id=" + id +
                ", registrationStatus=" + registrationStatus +
                ", qrCodeValue='" + qrCodeValue + '\'' +
                ", registeredAt=" + registeredAt +
                ", cancelledAt=" + cancelledAt +
                '}';
    }
}
