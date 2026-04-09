package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.MembershipApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository cho MembershipApplication entity.
 */
@Repository
public interface MembershipApplicationRepository extends JpaRepository<MembershipApplication, UUID> {

    /**
     * Tìm các đơn đăng ký theo account ID.
     */
    List<MembershipApplication> findByAccountId(UUID accountId);

    /**
     * Tìm đơn đăng ký theo email người nộp.
     */
    List<MembershipApplication> findByApplicantEmail(String applicantEmail);
}
