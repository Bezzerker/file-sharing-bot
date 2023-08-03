package ru.zerrbild.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ResponseConsumerService {
    void consume(SendMessage sendMessage);
}
