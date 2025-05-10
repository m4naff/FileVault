package com.devlab.filevault.service;


import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service interface for handling file uploads.
 */
public interface FileUploadService {

    /**
     * Uploads a file with an optional password and expiration time.
     *
     * @param file         The file to be uploaded.
     * @param password     The password for the file (optional).
     * @param expiresInHours The expiration time in hours (optional).
     * @return The ID of the uploaded file.
     */
    String uploadFile(MultipartFile file, String password, int expiresInHours, int downloadLimit);

}
