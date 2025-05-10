package com.devlab.filevault.controller;

import com.devlab.filevault.service.FileDownloadService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("download")
public class FileDownloadController {

    private final FileDownloadService  fileDownloadService;

    @GetMapping("{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable String fileId,
            @RequestParam(value = "password", required = false) String password) throws Exception {
        return fileDownloadService.downloadFile(fileId, password);
    }

}
