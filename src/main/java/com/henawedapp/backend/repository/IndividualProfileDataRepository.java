package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.IndividualProfileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho IndividualProfileData entity.
 */
@Repository
public interface IndividualProfileDataRepository extends JpaRepository<IndividualProfileData, UUID> {

    /**
     * Tìm IndividualProfileData theo PendingRegistration ID.
     */
    Optional<IndividualProfileData> findByPendingRegistrationId(UUID pendingRegistrationId);
}
