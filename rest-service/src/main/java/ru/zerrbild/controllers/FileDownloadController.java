package ru.zerrbild.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.zerrbild.services.FileDataService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/files")
@RestController
public class FileDownloadController {
    private final FileDataService fileDataService;

    @GetMapping("/document")
    public ResponseEntity<InputStreamResource> downloadFile(@RequestParam String id,
                                          @RequestParam(defaultValue = "attachment", required = false) String disposition) {
        var doc = fileDataService.getDocumentByEncryptedId(id);
        InputStreamResource resource = fileDataService.getFileResource(doc.getBinaryData());
        String encodedFileName = URLEncoder.encode(doc.getFileName(), StandardCharsets.UTF_8).replace("+", " ");
        return ResponseEntity.ok()
                .header("Content-Disposition", String.format("%s; filename=\"%s\"", disposition, encodedFileName))
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .contentLength(doc.getFileSize())
                .contentType(MediaType.parseMediaType(doc.getMimeType()))
                .body(resource);
    }

    @GetMapping("/image")
    public ResponseEntity<InputStreamResource> downloadImage(@RequestParam String id,
                                           @RequestParam(defaultValue = "attachment", required = false) String disposition) {
        var image = fileDataService.getImageByEncryptedId(id);
        InputStreamResource resource = fileDataService.getFileResource(image.getBinaryData());
        return ResponseEntity.ok()
                .header("Content-Disposition", String.format("%s; filename=\"%s.jpg\"", disposition, image.getFileId()))
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .contentLength(image.getFileSize())
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}