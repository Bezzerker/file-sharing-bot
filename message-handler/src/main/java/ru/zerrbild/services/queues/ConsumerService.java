package ru.zerrbild.services.queues;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface ConsumerService {
    void consumeUpdateWithTextMessage(Update update);
    void consumeUpdateWithDocumentMessage(Update update);
    void consumeUpdateWithImageMessage(Update update);
    void consumeDeregisterCandidateId(Long userId);
}