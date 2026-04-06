package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

/**
 * Account entity - Ánh xạ tới bảng "Account" trong database.
 * Sử dụng Jakarta Persistence API (jakarta.persistence).
 */
@Entity
@Data
@Table(name = "Account")
public class Account {

    // ============================================================
    // ENUMS
    // ============================================================

    /**
     * Enum vai trò tài khoản.
     */
    public enum AccountRole {
        USER,
        ADMIN,
        MODERATOR
    }

    /**
     * Enum trạng thái tài khoản.
     */
    public enum AccountStatus {
        ACTIVE,
        INACTIVE,
        SUSPENDED,
        PENDING
    }

    // ============================================================
    // FIELDS
    // ============================================================

    /**
     * Khóa chính: UUID 16-byte dạng binary.
     * @Id đánh dấu đây là khóa chính
     * @GeneratedValue với chiến lược UUID để tự động sinh UUID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Email người dùng, phải unique và not null.
     */
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    /**
     * Số điện thoại người dùng, unique và không bắt buộc.
     */
    @Column(name = "phone", unique = true, length = 50)
    private String phone;

    /**
     * Mật khẩu đã băm, lưu dưới dạng TEXT.
     */
    @Column(name = "password_hash", nullable = false, columnDefinition = "TEXT")
    private String passwordHash;

    /**
     * Vai trò tài khoản với giá trị mặc định USER.
     * @Enumerated dạng STRING để lưu tên enum (VD: "USER") vào DB
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private AccountRole role = AccountRole.USER;

    /**
     * Trạng thái tài khoản với giá trị mặc định ACTIVE.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AccountStatus status = AccountStatus.ACTIVE;

    /**
     * Cờ kích hoạt: 0 = chưa kích hoạt, 1 = đã kích hoạt.
     * @Column columnDefinition dùng TINYINT(1) để khớp schema DB
     */
    @Column(name = "is_activated", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isActivated = false;

    /**
     * Dấu thời gian đăng nhập lần cuối thành công, độ chính xác mili-giây.
     */
    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    /**
     * Dấu thời gian tạo bản ghi, độ chính xác mili-giây.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Dấu thời gian cập nhật bản ghi lần cuối, độ chính xác mili-giây.
     */
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ============================================================
    // CONSTRUCTORS
    // ============================================================

    /**
     * Constructor mặc định (no-args) cần thiết cho JPA.
     */
    protected Account() {
    }

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    /**
     * Tự động set created_at trước khi persist entity mới.
     */
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * Tự động cập nhật updated_at trước khi cập nhật entity đã tồn tại.
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public Boolean getIsActivated() {
        return isActivated;
    }

    public void setIsActivated(Boolean isActivated) {
        this.isActivated = isActivated;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
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
        return "Account{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", isActivated=" + isActivated +
                ", lastLoginAt=" + lastLoginAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
