package ru.zerrbild.services.message.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zerrbild.entities.UserEntity;
import ru.zerrbild.services.message.NotificationService;
import ru.zerrbild.services.queues.ProducerService;

@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    @Value("${rabbitmq.exchanges.message.name}")
    private String messageExchange;
    @Value("${rabbitmq.exchanges.message.queues.response.routing_key}")
    private String responseRoutingKey;
    private final ProducerService responseProducer;

    @Override
    public void notifyUser(Update update, String message) {
        var chatId = update.getMessage().getChatId();
        notifyUser(chatId, message);
    }

    @Override
    public void notifyUser(Long chatId, String message) {
        var sendMessage = SendMessage.builder()
                .chatId(chatId)
                .text(message)
                .parseMode(ParseMode.HTML)
                .build();

        responseProducer.produceResponse(messageExchange, responseRoutingKey, sendMessage);
    }

    @Override
    public void replyToUser(Update update, String message) {
        var userMessage = update.getMessage();
        var chatId = userMessage.getChatId();
        var replyMessageId = userMessage.getMessageId();

        var sendMessage = SendMessage.builder()
                .chatId(chatId)
                .replyToMessageId(replyMessageId)
                .text(message)
                .parseMode(ParseMode.HTML)
                .build();

        responseProducer.produceResponse(messageExchange, responseRoutingKey, sendMessage);
    }

    @Override
    public void notifyRegistrationCancellation(UserEntity user) {
        notifyUser(user.getTgUserId(),
                "Вы не подтвердили Ваш электронный адрес в течение часа — <b>регистрация отменена!</b>" +
                        "\nОтправьте /help чтобы посмотреть список доступных команд");
    }

    @Override
    public void notifyRegistrationCompletion(Long telegramUserId) {
        notifyUser(telegramUserId,
                "<b>Электронный адрес подтвержден!</b>\nМожете отправлять файлы и изображения!");
    }
}