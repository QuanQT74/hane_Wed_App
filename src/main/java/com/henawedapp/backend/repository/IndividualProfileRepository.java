package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.IndividualProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho IndividualProfile entity.
 */
@Repository
public interface IndividualProfileRepository extends JpaRepository<IndividualProfile, UUID> {

    /**
     * Tìm individual profile theo member profile ID.
     */
    Optional<IndividualProfile> findByMemberProfileId(UUID memberProfileId);
}
