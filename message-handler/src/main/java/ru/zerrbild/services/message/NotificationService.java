package ru.zerrbild.services.message;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface NotificationService {
    void notifyUser(Long chatId, String message);
    void notifyUser(Update update, String message);
    void replyToUser(Update update, String message);
}