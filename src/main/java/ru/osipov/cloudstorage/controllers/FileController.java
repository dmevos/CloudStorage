package ru.osipov.cloudstorage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.osipov.cloudstorage.dto.FileDTO;
import ru.osipov.cloudstorage.services.FileService;

import java.io.IOException;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestHeader("auth-token") String authToken,
                                        String filename,
                                        @RequestParam MultipartFile file) throws IOException {

        fileService.uploadFile(authToken, filename, file);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @DeleteMapping
    public void deleteFile(@RequestHeader("auth-token") String authToken,
                           @RequestParam("filename") String filename) {
        fileService.deleteFile(authToken, filename);
    }

    @GetMapping
    public ResponseEntity<Resource> downloadFile(@RequestHeader("auth-token") String authToken,
                                                 @RequestParam("filename") String filename) {
        byte[] file = fileService.downloadFile(authToken, filename).getContent();
        return ResponseEntity.ok().body(new ByteArrayResource(file));
    }

    @PutMapping
    public ResponseEntity<?> renameFile(@RequestHeader("auth-token") String authToken,
                                        @RequestParam("filename") String filename,
                                        @RequestBody FileDTO newFileName) {
        fileService.renameFile(authToken, filename, newFileName.getFilename());
        return ResponseEntity.ok(HttpStatus.OK);
    }

}