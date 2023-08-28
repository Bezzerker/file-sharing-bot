package ru.zerrbild.services;

import org.springframework.core.io.InputStreamResource;
import ru.zerrbild.entities.BinaryDataEntity;
import ru.zerrbild.entities.DocumentEntity;
import ru.zerrbild.entities.ImageEntity;

public interface FileDataService {
    DocumentEntity getDocumentByEncryptedId(String encryptedId);
    ImageEntity getImageByEncryptedId(String encryptedId);
    InputStreamResource getFileResource(BinaryDataEntity binaryData);
}