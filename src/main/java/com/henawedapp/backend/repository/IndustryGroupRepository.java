package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.IndustryGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho IndustryGroup entity.
 */
@Repository
public interface IndustryGroupRepository extends JpaRepository<IndustryGroup, UUID> {

    /**
     * Tìm industry group theo code.
     */
    Optional<IndustryGroup> findByCode(String code);

    /**
     * Kiểm tra code đã tồn tại chưa.
     */
    boolean existsByCode(String code);
}
