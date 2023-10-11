package ru.zerrbild.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import ru.zerrbild.dao.DocumentDAO;
import ru.zerrbild.dao.ImageDAO;
import ru.zerrbild.entities.BinaryDataEntity;
import ru.zerrbild.entities.DocumentEntity;
import ru.zerrbild.entities.ImageEntity;
import ru.zerrbild.exceptions.RequestedFileNotFoundException;
import ru.zerrbild.services.FileDataService;
import ru.zerrbild.utils.ciphering.Decoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileDataServiceImpl implements FileDataService {
    @Value("${link.decryption_key}")
    private String key;
    private final DocumentDAO documentDAO;
    private final ImageDAO imageDAO;
    private final Decoder decoder;

    @Override
    public DocumentEntity getDocumentByEncryptedId(String encodedId) {
        Long id = decoder.decodeToLong(encodedId, key);
        return documentDAO.findById(id)
                .orElseThrow(() -> new RequestedFileNotFoundException(
                        String.format("The requested document with id = '%s' is not in the database", id)));
    }

    @Override
    public ImageEntity getImageByEncryptedId(String encodedId) {
        Long id = decoder.decodeToLong(encodedId, key);
        return imageDAO.findById(id)
                .orElseThrow(() -> new RequestedFileNotFoundException(
                        String.format("The requested image with id = '%s' is not in the database", id)));
    }

    public InputStreamResource getFileResource(BinaryDataEntity binaryData) {
        try {
            File file = File.createTempFile(UUID.randomUUID().toString(), ".tmp");
            file.deleteOnExit();

            try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                byte[] docBytes = binaryData.getBytes();
                fileOutputStream.write(docBytes);
            }

            FileInputStream fileInputStreamWithAutoDelete = new FileInputStream(file) {
                @Override
                public void close() throws IOException {
                    super.close();
                    Files.delete(file.toPath());
                }
            };

            return new InputStreamResource(fileInputStreamWithAutoDelete);
        } catch (IOException e) {
            log.error("Failed to create a temporary file from byte array and return InputStreamResource. {}", e.getMessage());
            return null;
        }
    }
}