package com.fileuploader.fileeditor.repo;

import com.fileuploader.fileeditor.data.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
}
