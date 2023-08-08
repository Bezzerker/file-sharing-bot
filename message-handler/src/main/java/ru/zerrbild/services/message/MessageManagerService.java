package ru.zerrbild.services.message;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MessageManagerService {
    void processTextMessage(Update update);
    void processDocumentMessage(Update update);
    void processImageMessage(Update update);
}
