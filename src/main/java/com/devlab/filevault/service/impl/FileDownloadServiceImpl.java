package com.devlab.filevault.service.impl;

import com.devlab.filevault.exception.InvalidPasswordException;
import com.devlab.filevault.model.FileMetaData;
import com.devlab.filevault.repository.FileMetaDataRepository;
import com.devlab.filevault.service.FileDownloadService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FileDownloadServiceImpl implements FileDownloadService {

    private final FileMetaDataRepository fileMetaDataRepository;

    private final PasswordEncoder passwordEncoder;

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;


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
            throw new IllegalArgumentException("File has reached its maximum download limit");
        }

        if (password != null && !passwordEncoder.matches(password, fileMetaData.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
    }

}
