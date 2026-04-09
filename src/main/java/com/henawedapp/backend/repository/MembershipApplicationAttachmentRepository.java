package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.MembershipApplicationAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository cho MembershipApplicationAttachment entity.
 */
@Repository
public interface MembershipApplicationAttachmentRepository extends JpaRepository<MembershipApplicationAttachment, UUID> {

    /**
     * Tìm các attachment theo application ID.
     */
    List<MembershipApplicationAttachment> findByApplicationId(UUID applicationId);
}
