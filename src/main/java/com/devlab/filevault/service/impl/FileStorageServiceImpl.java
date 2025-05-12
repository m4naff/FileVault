package com.devlab.filevault.service.impl;

import com.devlab.filevault.exception.FileExpiredException;
import com.devlab.filevault.exception.InvalidPasswordException;
import com.devlab.filevault.model.FileMetaData;
import com.devlab.filevault.repository.FileMetaDataRepository;
import com.devlab.filevault.service.FileStorageService;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageServiceImpl implements FileStorageService {

    private final FileMetaDataRepository fileMetaDataRepository;

    private final PasswordEncoder passwordEncoder;

    private final MinioClient minioClient;

    private final static String URL = "localhost:8080/api/v1/download/";

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
                    .expirationDate(java.time.LocalDateTime.now().plusHours(expiresInHours))
                    .maxDownloads(downloadLimit > 0 ? downloadLimit : 5)
                    .password(password != null ? passwordEncoder.encode(password) : null)
                    .build();
            return URL + fileMetaDataRepository.save(fileMetaData).getId();
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while uploading file", e);
        }
    }

    /**
     * Downloads a file by its ID and verifies the password.
     *
     * @param fileId   The ID of the file to be downloaded.
     * @param password The password for the file (optional).
     * @return The byte array of the downloaded file.
     */
    @Override
    public ResponseEntity<Resource> downloadFile(String fileId, String password) throws Exception {

        FileMetaData fileMetaData = fileMetaDataRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("File not found"));

        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileMetaData.getObjectKey())
                        .build()
        );
        InputStreamResource resource = new InputStreamResource(stream);

        checkExpirationAndPasswordMatch(fileMetaData, password);
        fileMetaData.setMaxDownloads(fileMetaData.getMaxDownloads() - 1);
        fileMetaDataRepository.save(fileMetaData);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileMetaData.getFileType()))
                .header("Content-Disposition", "attachment; filename=")
                .body(resource);
    }

    private void checkExpirationAndPasswordMatch(FileMetaData fileMetaData, String password) throws Exception{
        if (fileMetaData.getExpirationDate().isBefore(LocalDateTime.now()) || fileMetaData.getMaxDownloads() <= 0) {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileMetaData.getObjectKey())
                            .build()
            );
            fileMetaDataRepository.deleteById(fileMetaData.getId());
            throw new FileExpiredException("File expired or download limit reached");
        }

        if (password != null && !passwordEncoder.matches(password, fileMetaData.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
    }

}
