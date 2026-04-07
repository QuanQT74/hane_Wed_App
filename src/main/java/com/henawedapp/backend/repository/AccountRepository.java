package com.henawedapp.backend.repository;

import com.henawedapp.backend.model.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository cho Account entity.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    /**
     * Tìm account theo email.
     */
    Optional<Account> findByEmail(String email);

    /**
     * Tìm account theo phone.
     */
    Optional<Account> findByPhone(String phone);

    /**
     * Kiểm tra email đã tồn tại chưa.
     */
    boolean existsByEmail(String email);

    /**
     * Kiểm tra phone đã tồn tại chưa.
     */
    boolean existsByPhone(String phone);
}
