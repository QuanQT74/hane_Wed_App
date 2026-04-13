package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.ProfileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho ProfileAttachment entity.
 */
@Repository
public interface ProfileAttachmentRepository extends JpaRepository<ProfileAttachment, UUID> {

    /**
     * Tìm tất cả attachments của một member profile.
     */
    List<ProfileAttachment> findByMemberProfileId(UUID memberProfileId);

    /**
     * Tìm attachment theo ID và member profile ID (để đảm bảo chỉ owner mới truy cập).
     */
    Optional<ProfileAttachment> findByIdAndMemberProfileId(UUID id, UUID memberProfileId);

    /**
     * Xóa attachment theo ID và member profile ID.
     */
    void deleteByIdAndMemberProfileId(UUID id, UUID memberProfileId);
}
