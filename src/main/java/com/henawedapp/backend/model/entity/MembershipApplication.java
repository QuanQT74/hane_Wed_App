package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.ApplicationStatus;
import com.henawedapp.backend.model.enums.MemberType;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MembershipApplication - Đơn đăng ký thành viên.
 */
@Entity
@Table(name = "membership_application")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"account", "reviewedBy", "attachments"})
public class MembershipApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "requested_member_type", nullable = false, length = 20)
    private MemberType requestedMemberType;

    @Column(name = "applicant_name", nullable = false)
    private String applicantName;

    @Column(name = "applicant_email", nullable = false)
    private String applicantEmail;

    @Column(name = "applicant_phone", length = 20)
    private String applicantPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    @Column(name = "reviewed_at")
    private Instant reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Account reviewedBy;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    // ========== Relationships ==========

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MembershipApplicationAttachment> attachments = new ArrayList<>();

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        if (this.submittedAt == null) {
            this.submittedAt = Instant.now();
        }
    }
}
