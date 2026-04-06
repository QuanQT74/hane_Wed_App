package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Event entity - Ánh xạ tới bảng "event".
 * Sự kiện trong hệ thống.
 */
@Entity
@Table(name = "event")
public class Event {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum trạng thái sự kiện.
     */
    public enum EventStatus {
        DRAFT,
        PUBLISHED,
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
     * Tiêu đề sự kiện.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Mô tả sự kiện.
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * ID loại sự kiện.
     */
    @Column(name = "event_type_id", columnDefinition = "BINARY(16)")
    private UUID eventTypeId;

    /**
     * Địa điểm.
     */
    @Column(name = "location_text", columnDefinition = "TEXT")
    private String locationText;

    /**
     * Thời gian bắt đầu.
     */
    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    /**
     * Thời gian kết thúc.
     */
    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    /**
     * Số người tham gia tối đa.
     */
    @Column(name = "max_capacity")
    private Integer maxCapacity;

    /**
     * Trạng thái sự kiện.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private EventStatus status = EventStatus.DRAFT;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected Event() {
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UUID getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(UUID eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getLocationText() {
        return locationText;
    }

    public void setLocationText(String locationText) {
        this.locationText = locationText;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }

    public Integer getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", eventTypeId=" + eventTypeId +
                ", locationText='" + locationText + '\'' +
                ", startAt=" + startAt +
                ", endAt=" + endAt +
                ", maxCapacity=" + maxCapacity +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
