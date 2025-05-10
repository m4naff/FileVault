package com.devlab.filevault.cron;

import com.devlab.filevault.model.FileMetaData;
import com.devlab.filevault.repository.FileMetaDataRepository;
import io.minio.MinioClient;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileExpirationScheduler {

    private final FileMetaDataRepository fileMetaDataRepository;

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    @Scheduled(cron = "0 0 * * * *")
    public void checkAndDeleteExpiredFiles() {
        List<FileMetaData> expiredFiles = fileMetaDataRepository.expiredFiles();

        try {
            for (FileMetaData fileMetaData: expiredFiles) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileMetaData.getObjectKey())
                                .build()
                );
                fileMetaDataRepository.delete(fileMetaData);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while deleting expired files", e);
        }
    }

}
