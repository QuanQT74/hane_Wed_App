package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.Otp;
import com.henawedapp.backend.model.enums.OtpStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho Otp entity.
 */
@Repository
public interface OtpRepository extends JpaRepository<Otp, UUID> {

    /**
     * Tìm OTP theo pending registration ID.
     */
    Optional<Otp> findByPendingRegistrationId(UUID pendingRegistrationId);

    /**
     * Tìm OTP đang hoạt động theo pending registration ID.
     */
    Optional<Otp> findByPendingRegistrationIdAndStatus(UUID pendingRegistrationId, OtpStatus status);

    /**
     * Xóa các OTP đã hết hạn.
     */
    @Query("DELETE FROM Otp o WHERE o.expiresAt < :now AND o.status = :status")
    void deleteExpiredOtps(@Param("now") Instant now, @Param("status") OtpStatus status);

    /**
     * Đếm số OTP đang hoạt động của một pending registration.
     */
    long countByPendingRegistrationIdAndStatus(UUID pendingRegistrationId, OtpStatus status);
}
