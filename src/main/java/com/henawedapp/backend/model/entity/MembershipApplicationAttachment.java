package com.henawedapp.backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * MembershipApplicationAttachment entity - Ánh xạ tới bảng "membership_application_attachment".
 * Tệp đính kèm của đơn đăng ký hội viên.
 */
@Entity
@Table(name = "membership_application_attachment")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MembershipApplicationAttachment {

    /**
     * Loại tệp đính kèm: ID_CARD, BUSINESS_LICENSE, PROOF_OF_ADDRESS, OTHER
     */
    public enum AttachmentType {
        ID_CARD,
        BUSINESS_LICENSE,
        PROOF_OF_ADDRESS,
        OTHER;

        @Override
        public String toString() {
            return name();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private MembershipApplication application;

    @Enumerated(EnumType.STRING)
    @Column(name = "attachment_type", nullable = false, length = 50)
    private AttachmentType attachmentType;

    @Column(name = "file_url", nullable = false, columnDefinition = "TEXT")
    private String fileUrl;

    @Column(name = "uploaded_at", nullable = false)
    private Instant uploadedAt;

    @PrePersist
    protected void onCreate() {
        if (this.uploadedAt == null) this.uploadedAt = Instant.now();
    }

    @Override
    public String toString() {
        return "MembershipApplicationAttachment{id=" + id + ", attachmentType=" + attachmentType + "}";
    }
}
