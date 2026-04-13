package com.henawedapp.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.*;

/**
 * Service quản lý token blacklist để invalidate token khi logout.
 * Sử dụng in-memory ConcurrentHashMap để lưu trữ.
 * Production nên thay bằng Redis.
 */
@Service
@Slf4j
public class TokenBlacklistService {

    private final ConcurrentHashMap<String, Instant> blacklist = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Thêm token vào blacklist.
     *
     * @param token JWT token
     * @param expirationMillis Thời gian còn lại trước khi token hết hạn
     */
    public void blacklistToken(String token, long expirationMillis) {
        // Thêm vào blacklist với thời điểm hết hạn
        blacklist.put(token, Instant.now().plusMillis(expirationMillis));
        log.info("Token blacklisted, will expire at {}", Instant.now().plusMillis(expirationMillis));

        // Schedule cleanup sau khi token hết hạn
        scheduler.schedule(() -> {
            blacklist.remove(token);
            log.debug("Token removed from blacklist");
        }, expirationMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Kiểm tra token có trong blacklist không.
     */
    public boolean isTokenBlacklisted(String token) {
        // Kiểm tra và dọn dẹp nếu đã hết hạn
        Instant expiration = blacklist.get(token);
        if (expiration != null && Instant.now().isAfter(expiration)) {
            blacklist.remove(token);
            return false;
        }
        return expiration != null;
    }
}