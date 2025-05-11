package com.devlab.filevault.controller;

import com.devlab.filevault.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Tag(name = "File Operations", description = "APIs for uploading and downloading files with security constraints")
public class FileController {

    private final FileStorageService fileStorageService;

    @Operation(
            summary = "Upload a file",
            description = "Upload a file with password protection, expiration time, and download limit",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "File uploaded successfully",
                            content = @Content(mediaType = "text/plain", schema = @Schema(implementation = String.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request if file is empty or parameters are invalid"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @PostMapping("upload")
    public String uploadFile(
            @Parameter(description = "File to upload", required = true, content = @Content(mediaType = "multipart/form-data"))
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "Password for file protection", required = true, example = "mySecurePassword123")
            @RequestParam(value = "password") String password,

            @Parameter(description = "Expiration time in hours (0 for no expiration)", required = true, example = "24")
            @RequestParam(value = "expiresInHours") int expiresInHours,

            @Parameter(description = "Maximum number of downloads allowed (default 5)", example = "5")
            @RequestParam(value = "downloadLimit", defaultValue = "5") int downloadLimit) {
        return fileStorageService.uploadFile(file, password, expiresInHours, downloadLimit);
    }

    @Operation(
            summary = "Download a file",
            description = "Download a file by its ID, optionally providing a password if the file is protected",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "File downloaded successfully",
                            content = @Content(mediaType = "application/octet-stream")
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized if password is required but not provided or incorrect"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "File not found or expired"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Download limit exceeded"
                    )
            }
    )
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "ID of the file to download", required = true, example = "abc123-def456")
            @PathVariable String fileId,

            @Parameter(description = "Password for protected files", example = "mySecurePassword123")
            @RequestParam(value = "password", required = false) String password) throws Exception {
        return fileStorageService.downloadFile(fileId, password);
    }
}
