package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * ProfileAttachment entity - Ánh xạ tới bảng "profile_attachment".
 * Tệp đính kèm của hồ sơ hội viên.
 */
@Entity
@Table(name = "profile_attachment")
public class ProfileAttachment {

    // ============================================================
    // FIELDS
    // ============================================================

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    /**
     * Hồ sơ hội viên sở hữu tệp đính kèm.
     * @ManyToOne thiết lập quan hệ nhiều-1 với MemberProfile
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    /**
     * Loại tệp đính kèm.
     */
    @Column(name = "attachment_type", nullable = false, length = 100)
    private String attachmentType;

    /**
     * URL của tệp.
     */
    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    /**
     * Cờ công khai.
     */
    @Column(name = "is_public", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isPublic = false;

    /**
     * Thời gian tải lên.
     */
    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    // ============================================================
    // LIFECYCLE CALLBACKS
    // ============================================================

    @PrePersist
    protected void onCreate() {
        if (this.uploadedAt == null) this.uploadedAt = Instant.now();
    }

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    protected ProfileAttachment() {
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

    public MemberProfile getMemberProfile() {
        return memberProfile;
    }

    public void setMemberProfile(MemberProfile memberProfile) {
        this.memberProfile = memberProfile;
    }

    public String getAttachmentType() {
        return attachmentType;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    // ============================================================
    // TOSTRING
    // ============================================================

    @Override
    public String toString() {
        return "ProfileAttachment{" +
                "id=" + id +
                ", attachmentType='" + attachmentType + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", isPublic=" + isPublic +
                ", uploadedAt=" + uploadedAt +
                '}';
    }
}
