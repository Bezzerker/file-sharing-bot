package ru.zerrbild.services.queues.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zerrbild.services.message.MessageManagerService;
import ru.zerrbild.services.queues.ConsumerService;

@Slf4j
@RequiredArgsConstructor
@Service
public class ConsumerServiceImpl implements ConsumerService {
    private final MessageManagerService messageManagerService;

    @Override
    @RabbitListener(queues = "${rabbitmq.queue.text}")
    public void consumeUpdateWithTextMessage(Update update) {
        log.info("Received update containing text from RabbitMQ queue");
        messageManagerService.processTextMessage(update);
    }

    @Override
    @RabbitListener(queues = "${rabbitmq.queue.document}")
    public void consumeUpdateWithDocumentMessage(Update update) {
        log.info("Received update containing document from RabbitMQ queue");
        messageManagerService.processDocumentMessage(update);
    }

    @Override
    @RabbitListener(queues = "${rabbitmq.queue.image}")
    public void consumeUpdateWithImageMessage(Update update) {
        log.info("Received update containing image from RabbitMQ queue");
        messageManagerService.processImageMessage(update);
    }
}