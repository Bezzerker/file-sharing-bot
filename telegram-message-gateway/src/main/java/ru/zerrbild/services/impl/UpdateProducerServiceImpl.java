package ru.zerrbild.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zerrbild.services.UpdateProducerService;

@RequiredArgsConstructor
@Slf4j
@Service
public class UpdateProducerServiceImpl implements UpdateProducerService {
    public final RabbitTemplate rabbitTemplate;

    @Override
    public void produce(String exchange, String routingKey, Update update) {
        var message = update.getMessage();
        var messageText = message.getText();
        var userId = message.getFrom().getId();
        log.info("An update from user \"{}\" with message \"{}\" is added to the " +
                "queue by the following routing key \"{}\"", userId, messageText, routingKey);
        rabbitTemplate.convertAndSend(exchange, routingKey, update);
    }
}
