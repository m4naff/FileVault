package com.devlab.filevault.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "files")
/**
 * Entity class representing the metadata of a file.
 */
public class FileMetaData {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "original_file_name", nullable = false)
    private String fileName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_size")
    private long fileSize;

    @Column(name = "object_key", nullable = false, unique = true)
    private String objectKey;

    @Column(name = "upload_date")
    private LocalDateTime uploadDate;

    @Builder.Default
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate = LocalDateTime.now().plusDays(1);

    @Column(name = "max_downloads")
    @Builder.Default
    private int maxDownloads = 5;

    @Column(name = "password")
    private String password;
}
