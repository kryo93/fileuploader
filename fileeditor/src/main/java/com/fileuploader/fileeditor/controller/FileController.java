package com.fileuploader.fileeditor.controller;
import com.fileuploader.fileeditor.data.FileMetadata;
import com.fileuploader.fileeditor.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        FileMetadata uploadedFile = fileService.uploadFile(file, fileName);
        return ResponseEntity.ok(uploadedFile);
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long fileId) throws IOException {
        byte[] fileContent = fileService.getFile(fileId);
        return ResponseEntity.ok()
                .body(fileContent);
    }

    @PutMapping("/{fileId}")
    public ResponseEntity<FileMetadata> updateFile(@PathVariable Long fileId, @RequestParam("file") MultipartFile file)
            throws IOException {
        FileMetadata updatedFile = fileService.updateFile(fileId, file);
        return ResponseEntity.ok(updatedFile);
    }

    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) throws IOException {
        fileService.deleteFile(fileId);
        return ResponseEntity.ok("File deleted successfully");
    }

    @GetMapping
    public ResponseEntity<List<FileMetadata>> listFiles() {
        List<FileMetadata> fileList = fileService.listFiles();
        return ResponseEntity.ok(fileList);
    }
}
