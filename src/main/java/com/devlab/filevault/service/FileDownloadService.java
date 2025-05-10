package com.devlab.filevault.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;

/**
 * Service interface for handling file downloads.
 */
public interface FileDownloadService {

    /**
     * Downloads a file by its ID and verifies the password.
     *
     * @param fileId   The ID of the file to be downloaded.
     * @param password The password for the file (optional).
     * @return FileMetaData object containing file information.
     */
    ResponseEntity<Resource> downloadFile(String fileId, String password) throws Exception;

}
