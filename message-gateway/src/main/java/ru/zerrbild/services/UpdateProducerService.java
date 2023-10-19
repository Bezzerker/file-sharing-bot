package ru.zerrbild.services;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateProducerService {
    void produce(String exchange, String routingKey, Update update);
}
