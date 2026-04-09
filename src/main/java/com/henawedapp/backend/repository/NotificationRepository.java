package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository cho Notification entity.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * Tìm các notification theo created by (account ID).
     */
    List<Notification> findByCreatedById(UUID accountId);
}
