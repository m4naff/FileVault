package com.devlab.filevault.service.impl;

import com.devlab.filevault.model.FileMetaData;
import com.devlab.filevault.repository.FileMetaDataRepository;
import com.devlab.filevault.service.FileUploadService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileUploadServiceImpl implements FileUploadService {

    private final FileMetaDataRepository fileMetaDataRepository;

    private final PasswordEncoder passwordEncoder;

    private final MinioClient minioClient;

    private final static String URL = "localhost:8080/download/";

    @Value("${minio.bucket}")
    private String bucketName;


    /**
     * Uploads a file with an optional password and expiration time.
     *
     * @param file           The file to be uploaded.
     * @param password       The password for the file (optional).
     * @param expiresInHours The expiration time in hours (optional).
     * @return The ID of the uploaded file.
     */
    @Override
    public String uploadFile(MultipartFile file, String password, int expiresInHours, int downloadLimit) {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
            String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build());
            }
            FileMetaData fileMetaData = FileMetaData.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .objectKey(fileName)
                    .uploadDate(java.time.LocalDateTime.now())
                    .expirationDate(java.time.LocalDateTime.now().plusDays(expiresInHours))
                    .maxDownloads(downloadLimit > 0 ? downloadLimit : 5)
                    .password(password != null ? passwordEncoder.encode(password) : null)
                    .build();
            return URL + fileMetaDataRepository.save(fileMetaData).getId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while uploading file", e);
        }
    }
}
