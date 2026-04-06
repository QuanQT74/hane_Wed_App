package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * EventCheckin entity - Ánh xạ tới bảng "event_checkin".
 * Checkin tham gia sự kiện.
 */
@Entity
@Table(name = "event_checkin")
public class EventCheckin {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Đăng ký sự kiện.
     * @OneToOne thiết lập quan hệ 1-1 với EventRegistration
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registration_id", unique = true, nullable = false)
    private EventRegistration registration;

    /**
     * Thời gian checkin.
     */
    @Column(name = "checked_in_at", nullable = false)
    private Instant checkedInAt;

    /**
     * Người thực hiện checkin.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_in_by")
    private Account checkedInBy;

    /**
     * Cờ hợp lệ.
     */
    @Column(name = "is_valid", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isValid = true;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.checkedInAt == null) this.checkedInAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected EventCheckin() {
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

    public EventRegistration getRegistration() {
        return registration;
    }

    public void setRegistration(EventRegistration registration) {
        this.registration = registration;
    }

    public Instant getCheckedInAt() {
        return checkedInAt;
    }

    public void setCheckedInAt(Instant checkedInAt) {
        this.checkedInAt = checkedInAt;
    }

    public Account getCheckedInBy() {
        return checkedInBy;
    }

    public void setCheckedInBy(Account checkedInBy) {
        this.checkedInBy = checkedInBy;
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "EventCheckin{" +
                "id=" + id +
                ", checkedInAt=" + checkedInAt +
                ", checkedInBy=" + (checkedInBy != null ? checkedInBy.getId() : null) +
                ", isValid=" + isValid +
                '}';
    }
}
