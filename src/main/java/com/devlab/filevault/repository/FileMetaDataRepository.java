package com.devlab.filevault.repository;

import com.devlab.filevault.model.FileMetaData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileMetaDataRepository extends JpaRepository<FileMetaData, String> {

    @Query(
            """
            SELECT f FROM FileMetaData f
            WHERE f.expirationDate < CURRENT_TIMESTAMP
            """
    )
    List<FileMetaData> expiredFiles();



}
