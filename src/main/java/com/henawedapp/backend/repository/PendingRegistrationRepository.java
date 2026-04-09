package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.PendingRegistration;
import com.henawedapp.backend.model.enums.PendingRegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho PendingRegistration entity.
 */
@Repository
public interface PendingRegistrationRepository extends JpaRepository<PendingRegistration, UUID> {

    /**
     * Tìm PendingRegistration theo contact value (email hoặc phone).
     */
    Optional<PendingRegistration> findByContactValue(String contactValue);

    /**
     * Tìm PendingRegistration đang chờ xác thực theo contact value.
     */
    Optional<PendingRegistration> findByContactValueAndStatus(
            String contactValue,
            PendingRegistrationStatus status);

    /**
     * Kiểm tra xem contact value đã tồn tại trong PendingRegistration chưa.
     */
    boolean existsByContactValue(String contactValue);

    /**
     * Kiểm tra xem contact value đã tồn tại với status cụ thể chưa.
     */
    boolean existsByContactValueAndStatus(
            String contactValue,
            PendingRegistrationStatus status);

    /**
     * Xóa các PendingRegistration đã hết hạn.
     */
    @Query("DELETE FROM PendingRegistration p WHERE p.expiresAt < :now AND p.status = :status")
    void deleteExpiredRegistrations(@Param("now") Instant now, @Param("status") PendingRegistrationStatus status);

    /**
     * Tìm PendingRegistration kèm OTP để load nhanh.
     */
    @Query("SELECT p FROM PendingRegistration p LEFT JOIN FETCH p.otp WHERE p.contactValue = :contactValue")
    Optional<PendingRegistration> findByContactValueWithOtp(@Param("contactValue") String contactValue);
}
