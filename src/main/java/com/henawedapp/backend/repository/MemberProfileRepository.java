package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.MemberProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho MemberProfile entity.
 */
@Repository
public interface MemberProfileRepository extends JpaRepository<MemberProfile, UUID> {

    /**
     * Tìm member profile theo account ID.
     */
    Optional<MemberProfile> findByAccountId(UUID accountId);

    /**
     * Tìm member profile theo member code.
     */
    Optional<MemberProfile> findByMemberCode(String memberCode);

    /**
     * Kiểm tra member code đã tồn tại chưa.
     */
    boolean existsByMemberCode(String memberCode);
}
