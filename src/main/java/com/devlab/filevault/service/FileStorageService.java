package com.devlab.filevault.service;


import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service interface for handling file uploads and downloads.
 */
public interface FileStorageService {

    /**
     * Uploads a file with an optional password and expiration time.
     *
     * @param file         The file to be uploaded.
     * @param password     The password for the file (optional).
     * @param expiresInHours The expiration time in hours (optional).
     * @return The ID of the uploaded file.
     */
    String uploadFile(MultipartFile file, String password, int expiresInHours, int downloadLimit);

    /**
     * Downloads a file by its ID and verifies the password.
     *
     * @param fileId   The ID of the file to be downloaded.
     * @param password The password for the file (optional).
     * @return FileMetaData object containing file information.
     */
    ResponseEntity<Resource> downloadFile(String fileId, String password) throws Exception;

}
