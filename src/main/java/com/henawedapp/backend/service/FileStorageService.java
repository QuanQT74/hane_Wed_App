package com.henawedapp.backend.service;

import com.henawedapp.backend.exception.InvalidFileException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

/**
 * Service xử lý upload và lưu file.
 */
@Service
@Slf4j
public class FileStorageService {

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of("image/jpeg", "image/jpg", "image/png");
    private static final Set<String> ALLOWED_DOCUMENT_TYPES = Set.of("application/pdf");
    private static final Set<String> ALLOWED_TYPES = Set.of(
            "image/jpeg", "image/jpg", "image/png", "application/pdf"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Value("${app.file.upload.dir:uploads}")
    private String uploadDir;

    /**
     * Validate file trước khi lưu.
     */
    public void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("File không được để trống.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new InvalidFileException(
                    "Định dạng file không hợp lệ. Chỉ cho phép: jpg, jpeg, png, pdf"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidFileException(
                    String.format("Kích thước file vượt quá giới hạn. Tối đa %dMB.", MAX_FILE_SIZE / (1024 * 1024))
            );
        }
    }

    /**
     * Lưu file và trả về URL.
     */
    public String storeFile(MultipartFile file, String subDir) {
        validateFile(file);

        try {
            // Tạo directory nếu chưa có
            Path uploadPath = Paths.get(uploadDir, subDir);
            Files.createDirectories(uploadPath);

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            // Lưu file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Trả về relative URL
            return "/" + subDir + "/" + uniqueFilename;

        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new InvalidFileException("Không thể lưu file. Vui lòng thử lại.");
        }
    }

    /**
     * Xóa file theo URL.
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return;
        }

        try {
            // Convert URL to path
            String filePathStr = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
            Path filePath = Paths.get(uploadDir, filePathStr);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Failed to delete file: {}", fileUrl, e);
        }
    }

    /**
     * Get full path for a file URL.
     */
    public Path getFullPath(String fileUrl) {
        String filePathStr = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
        return Paths.get(uploadDir, filePathStr);
    }
}