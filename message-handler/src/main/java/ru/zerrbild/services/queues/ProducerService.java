package ru.zerrbild.services.queues;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    void produceResponse(String exchange, String routingKey, SendMessage message);
}
