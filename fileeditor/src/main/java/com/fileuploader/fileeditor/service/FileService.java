package com.fileuploader.fileeditor.service;

import com.fileuploader.fileeditor.data.FileMetadata;
import com.fileuploader.fileeditor.repo.FileMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    private final FileMetadataRepository fileMetadataRepository;
    private final String fileStorageLocation = ""; // Change this to your desired storage location

    @Autowired
    public FileService(FileMetadataRepository fileMetadataRepository) {
        this.fileMetadataRepository = fileMetadataRepository;
    }

    public FileMetadata uploadFile(MultipartFile file, String fileName) throws IOException {
        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setFileName(fileName);
        fileMetadata.setCreatedAt(LocalDateTime.now());
        fileMetadata.setSize(file.getSize());
        fileMetadata.setFileType(file.getContentType());

        // Save file to storage
        String filePath = fileStorageLocation + fileName;
        Path destination = Path.of(filePath);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        fileMetadata.setFilePath(filePath);

        // Save file metadata to the database
        return fileMetadataRepository.save(fileMetadata);
    }

    public byte[] getFile(Long fileId) throws IOException {
        Optional<FileMetadata> optionalFileMetadata = fileMetadataRepository.findById(fileId);
        if (optionalFileMetadata.isPresent()) {
            FileMetadata fileMetadata = optionalFileMetadata.get();
            String filePath = fileMetadata.getFilePath();
            Path path = Path.of(filePath);
            return Files.readAllBytes(path);
        }
        throw new FileNotFoundException("File not found with ID: " + fileId);
    }

    public FileMetadata updateFile(Long fileId, MultipartFile file) throws IOException {
        Optional<FileMetadata> optionalFileMetadata = fileMetadataRepository.findById(fileId);
        if (optionalFileMetadata.isPresent()) {
            FileMetadata fileMetadata = optionalFileMetadata.get();

            fileMetadata.setFileName(file.getOriginalFilename());
            fileMetadata.setSize(file.getSize());
            fileMetadata.setFileType(file.getContentType());

            String filePath = fileMetadata.getFilePath();
            Path destination = Path.of(filePath);
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

            return fileMetadataRepository.save(fileMetadata);
        }
        throw new FileNotFoundException("File not found with ID: " + fileId);
    }

    public void deleteFile(Long fileId) throws IOException {
        Optional<FileMetadata> optionalFileMetadata = fileMetadataRepository.findById(fileId);
        if (optionalFileMetadata.isPresent()) {
            FileMetadata fileMetadata = optionalFileMetadata.get();

            String filePath = fileMetadata.getFilePath();
            Files.deleteIfExists(Path.of(filePath));

            fileMetadataRepository.deleteById(fileId);
        } else {
            throw new FileNotFoundException("File not found with ID: " + fileId);
        }
    }

    public List<FileMetadata> listFiles() {
        return fileMetadataRepository.findAll();
    }
}

