package ru.zerrbild.services.queues.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.zerrbild.services.queues.ProducerService;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProducerServiceImpl implements ProducerService {
    public final RabbitTemplate rabbitTemplate;

    @Override
    public void produceRegistrant(String exchange, String routingKey, Long userId) {
        log.info("The id - {} of the registering user was placed in the queue by the key - {}", userId, routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, userId);
    }

    @Override
    public void produceResponse(String exchange, String routingKey, SendMessage message) {
        log.info("The response was sent to the queue by the key - {}", routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}