package ru.zerrbild.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.zerrbild.services.UpdateProducerService;

@Slf4j
@Controller
public class UpdateProcessor {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.queue.text}")
    private String textQueue;
    @Value("${rabbitmq.queue.document}")
    private String documentQueue;
    @Value("${rabbitmq.queue.image}")
    private String imageQueue;
    private TelegramBot telegramBot;
    private final UpdateProducerService messageProducer;

    public UpdateProcessor(UpdateProducerService messageProducer) {
        this.messageProducer = messageProducer;
    }

    void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    void processUpdate(Update update) {
        if (update == null) {
            log.error("The update received is null");
            return;
        }

        if (update.hasMessage()) {
            defineAndProcessMessage(update);
        } else {
            log.error("Message not received");
        }
    }

    private void defineAndProcessMessage(Update update) {
        var message = update.getMessage();

        if (message.hasDocument()) {
            processDocumentMessage(update);
        } else if (message.hasPhoto()) {
            processImageMessage(update);
        } else if (message.hasText()) {
            processTextMessage(update);
        } else processOtherTypeOfMessage(message);
    }

    private void processTextMessage(Update update) {
        messageProducer.produce(exchange, textQueue, update);
    }

    private void processDocumentMessage(Update update) {
        sendMessageToUser(update, "Получен документ — происходит обработка!");
        messageProducer.produce(exchange, documentQueue, update);
    }

    private void processImageMessage(Update update) {
        sendMessageToUser(update, "Получена фотография — происходит обработка!");
        messageProducer.produce(exchange, imageQueue, update);
    }

    private void processOtherTypeOfMessage(Message message) {
        sendMessageToUser(message.getChatId(), """
            *Получен неверный тип сообщения!*
            Убедитесь, что вы отправили текст, фотографию или документ!""");
    }

    private void sendMessageToUser(Update update, String messageText) {
        var chatId = update.getMessage().getChatId();
        this.sendMessageToUser(chatId, messageText);
    }

    private void sendMessageToUser(Long chatId, String messageText) {
        var sentMessage = SendMessage.builder()
                .chatId(chatId)
                .text(messageText)
                .build();
        sentMessage.enableMarkdown(true);
        try {
            telegramBot.execute(sentMessage);
        } catch (TelegramApiException apiException) {
            log.error(apiException.getMessage());
            throw new RuntimeException(apiException.getMessage());
        }
    }
}