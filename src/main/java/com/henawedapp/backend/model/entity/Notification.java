package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Notification entity - Ánh xạ tới bảng "notification".
 * Thông báo trong hệ thống.
 */
@Entity
@Table(name = "notification")
public class Notification {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum kênh thông báo.
     */
    public enum Channel {
        IN_APP,
        EMAIL,
        SMS
    }

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Tiêu đề thông báo.
     */
    @Column(name = "title", nullable = false)
    private String title;

    /**
     * Nội dung thông báo.
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * Kênh gửi thông báo.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false, length = 50)
    private Channel channel;

    /**
     * Người tạo thông báo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private Account createdBy;

    /**
     * Thời gian tạo.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected Notification() {
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Account getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Account createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", channel=" + channel +
                ", createdAt=" + createdAt +
                '}';
    }
}
