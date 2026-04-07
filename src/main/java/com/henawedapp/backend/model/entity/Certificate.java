package com.henawedapp.backend.model.entity;

import com.henawedapp.backend.model.enums.CertificateStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Certificate - Chứng chỉ của hội viên.
 */
@Entity
@Table(name = "certificate")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"memberProfile", "certificateType", "issuedBy"})
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_profile_id", nullable = false)
    private MemberProfile memberProfile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certificate_type_id")
    private CertificateType certificateType;

    @Column(name = "certificate_no", unique = true, nullable = false, length = 100)
    private String certificateNo;

    @Column(name = "verification_code", unique = true, nullable = false, length = 100)
    private String verificationCode;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CertificateStatus status = CertificateStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issued_by")
    private Account issuedBy;
}
