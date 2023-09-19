package ru.zerrbild.services.message;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zerrbild.entities.UserEntity;

public interface NotificationService {
    void notifyUser(Long chatId, String message);
    void notifyUser(Update update, String message);
    void replyToUser(Update update, String message);
    void notifyRegistrationCancellation(UserEntity user);
    void notifyRegistrationCompletion(Long telegramUserId);
}