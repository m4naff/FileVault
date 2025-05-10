package com.devlab.filevault.controller;

import com.devlab.filevault.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("upload")
public class FileUploadController {

    private final FileUploadService fileUploadService;

    @PostMapping
    public String uploadFile(
            @RequestParam("file" ) MultipartFile file,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "expiresInHours") int expiresInHours,
            @RequestParam(value = "downloadLimit", defaultValue = "5") int downloadLimit) {
        return fileUploadService.uploadFile(file, password, expiresInHours, downloadLimit);
    }

}
