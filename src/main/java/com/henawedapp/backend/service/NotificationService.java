package com.henawedapp.backend.service;

import com.henawedapp.backend.model.entity.Account;
import com.henawedapp.backend.model.entity.Notification;
import com.henawedapp.backend.model.entity.NotificationRecipient;
import com.henawedapp.backend.model.enums.DeliveryStatus;
import com.henawedapp.backend.model.enums.NotificationChannel;
import com.henawedapp.backend.repository.NotificationRecipientRepository;
import com.henawedapp.backend.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Service xử lý thông báo.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    /**
     * Gửi thông báo cho một tài khoản.
     */
    @Transactional
    public Notification sendNotification(
            Account recipient,
            String title,
            String content,
            NotificationChannel channel) {

        log.info("Sending notification to account: {}, channel: {}", recipient.getId(), channel);

        // Tạo notification
        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .channel(channel)
                .createdAt(Instant.now())
                .build();

        notification = notificationRepository.save(notification);

        // Tạo notification recipient
        NotificationRecipient recipientEntry = NotificationRecipient.builder()
                .notification(notification)
                .account(recipient)
                .deliveryStatus(DeliveryStatus.PENDING)
                .build();

        notificationRecipientRepository.save(recipientEntry);

        log.info("Notification sent successfully, notificationId: {}", notification.getId());
        return notification;
    }

    /**
     * Gửi thông báo cho nhiều tài khoản.
     */
    @Transactional
    public void sendNotificationToMultiple(
            List<Account> recipients,
            String title,
            String content,
            NotificationChannel channel) {

        Notification notification = Notification.builder()
                .title(title)
                .content(content)
                .channel(channel)
                .createdAt(Instant.now())
                .build();

        notification = notificationRepository.save(notification);

        for (Account recipient : recipients) {
            NotificationRecipient recipientEntry = NotificationRecipient.builder()
                    .notification(notification)
                    .account(recipient)
                    .deliveryStatus(DeliveryStatus.PENDING)
                    .build();

            notificationRecipientRepository.save(recipientEntry);
        }

        log.info("Sent notification to {} recipients, notificationId: {}", recipients.size(), notification.getId());
    }

    /**
     * Gửi email notification đơn giản (mock implementation).
     * Trong thực tế sẽ tích hợp với email service thực sự.
     */
    @Transactional
    public void sendEmailNotification(Account account, String subject, String body) {
        log.info("Mock email sent to: {}, subject: {}", account.getEmail(), subject);
        // Trong thực tế: gọi email service như SendGrid, AWS SES, etc.
    }
}
