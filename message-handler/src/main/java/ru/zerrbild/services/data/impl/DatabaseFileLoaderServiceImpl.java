package ru.zerrbild.services.data.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zerrbild.dao.BinaryDataDAO;
import ru.zerrbild.dao.DocumentDAO;
import ru.zerrbild.dao.ImageDAO;
import ru.zerrbild.entities.BinaryDataEntity;
import ru.zerrbild.entities.DocumentEntity;
import ru.zerrbild.entities.ImageEntity;
import ru.zerrbild.exceptions.FileDownloadException;
import ru.zerrbild.exceptions.JsonKeyNotFoundException;
import ru.zerrbild.exceptions.ResourceNotFoundException;
import ru.zerrbild.services.data.DatabaseFileLoaderService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class DatabaseFileLoaderServiceImpl implements DatabaseFileLoaderService {
    @Value("${telegram.api_uri.file_download}")
    private String downloadUri;
    @Value("${telegram.api_uri.file_info}")
    private String fileInfoUri;
    @Value("${telegram.bot.token}")
    private String botToken;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DocumentDAO documentDAO;
    private final ImageDAO imageDAO;
    private final BinaryDataDAO binaryDataDAO;

    @Override
    public DocumentEntity loadDocument(Update update) {
        var document = update.getMessage().getDocument();
        byte[] fileBytes = getFileBytes(document.getFileId());
        var persistentBinaryData = saveFileToDataBase(fileBytes);
        return saveDocumentInfoToDataBase(document, persistentBinaryData);
    }

    @Override
    public ImageEntity loadImage(Update update) {
        var photos = update.getMessage().getPhoto();
        var photo = photos.stream()
                .max(Comparator.comparingInt(PhotoSize::getFileSize))
                .orElseThrow();
        byte[] fileBytes = getFileBytes(photo.getFileId());
        var persistentBinaryData = saveFileToDataBase(fileBytes);
        return saveImageInfoToDataBase(photo, persistentBinaryData);
    }

    private DocumentEntity saveDocumentInfoToDataBase(Document document, BinaryDataEntity binaryData) {
        var savedDocument = DocumentEntity.builder()
                .fileId(document.getFileId())
                .fileName(document.getFileName())
                .fileSize(document.getFileSize())
                .mimeType(document.getMimeType())
                .binaryData(binaryData)
                .build();
        return documentDAO.save(savedDocument);
    }

    private ImageEntity saveImageInfoToDataBase(PhotoSize photoSize, BinaryDataEntity binaryData) {
        var savedImage = ImageEntity.builder()
                .fileId(photoSize.getFileId())
                .fileSize(photoSize.getFileSize())
                .binaryData(binaryData)
                .build();
        return imageDAO.save(savedImage);
    }

    private BinaryDataEntity saveFileToDataBase(byte[] fileBytes) {
        var savedBinaryData = BinaryDataEntity.builder()
                .bytes(fileBytes)
                .build();
        return binaryDataDAO.save(savedBinaryData);
    }

    private byte[] getFileBytes(String fileId) {
        var filePathParam = getFilePath(fileId);
        var readyDownloadUri = downloadUri
                .replace("{token}", botToken)
                .replace("{filePath}", filePathParam);
        try (InputStream inputStream = new URL(readyDownloadUri).openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new FileDownloadException("Failed to download file", e);
        }
    }

    private String getFilePath(String fileId) {
        var jsonFileInfo = getJsonFileInfo(fileId);
        return extractFilePath(jsonFileInfo);
    }

    private String getJsonFileInfo(String fileId) {
        try {
            Optional<ResponseEntity<String>> fileInfoResponse = Optional.of(
                    restTemplate.exchange(fileInfoUri,
                            HttpMethod.GET,
                            null,
                            String.class,
                            botToken, fileId
                    )
            );
            return fileInfoResponse.filter(r -> r.getStatusCode() == HttpStatus.OK)
                            .map(ResponseEntity::getBody)
                            .orElseThrow(() -> new ResourceNotFoundException("Request body is missing in the response"));
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new ResourceNotFoundException("The status of the response to the file information retrieval request is not OK", e);
        }
    }

    private String extractFilePath(String jsonFileInfo) {
        try {
            var jsonTree = objectMapper.readTree(jsonFileInfo);
            var filePath = Optional.ofNullable(jsonTree.findValue("file_path"));

            return filePath.map(JsonNode::textValue)
                    .orElseThrow(() -> new JsonKeyNotFoundException("The key file_path was not found"));
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new JsonKeyNotFoundException("Invalid JSON data format", e);
        }
    }
}