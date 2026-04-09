package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.OrganizationProfileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho OrganizationProfileData entity.
 */
@Repository
public interface OrganizationProfileDataRepository extends JpaRepository<OrganizationProfileData, UUID> {

    /**
     * Tìm OrganizationProfileData theo PendingRegistration ID.
     */
    Optional<OrganizationProfileData> findByPendingRegistrationId(UUID pendingRegistrationId);
}
