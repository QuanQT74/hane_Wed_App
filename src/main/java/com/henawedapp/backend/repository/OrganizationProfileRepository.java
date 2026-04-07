package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.OrganizationProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho OrganizationProfile entity.
 */
@Repository
public interface OrganizationProfileRepository extends JpaRepository<OrganizationProfile, UUID> {

    /**
     * Tìm organization profile theo member profile ID.
     */
    Optional<OrganizationProfile> findByMemberProfileId(UUID memberProfileId);
}
