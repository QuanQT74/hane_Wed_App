package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository cho NotificationRecipient entity.
 */
@Repository
public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, UUID> {

    /**
     * Tìm các notification recipient theo account ID.
     */
    List<NotificationRecipient> findByAccountId(UUID accountId);

    /**
     * Tìm các notification recipient theo notification ID.
     */
    List<NotificationRecipient> findByNotificationId(UUID notificationId);
}
