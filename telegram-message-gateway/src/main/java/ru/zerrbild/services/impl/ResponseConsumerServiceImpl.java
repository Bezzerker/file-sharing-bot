package ru.zerrbild.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.zerrbild.controllers.TelegramBot;
import ru.zerrbild.services.ResponseConsumerService;

@Slf4j
@RequiredArgsConstructor
@Service
public class ResponseConsumerServiceImpl implements ResponseConsumerService {
    private final TelegramBot telegramBot;

    @RabbitListener(queues = "${rabbitmq.queue.response}")
    public void consume(SendMessage sendMessage) {
        log.info("Received a response for user - {} with the following text: {}",
                sendMessage.getChatId(), sendMessage.getText());
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
