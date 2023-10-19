package ru.zerrbild.controllers;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Controller
public class TelegramBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botName;
    private final UpdateProcessor updateProcessor;

    protected TelegramBot(@Value("${bot.token}") String botToken,
                          UpdateProcessor updateProcessor) {
        super(botToken);
        this.updateProcessor = updateProcessor;
    }

    @PostConstruct
    public void initUpdateProcessor() {
        updateProcessor.setTelegramBot(this);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateProcessor.processUpdate(update);
    }
}
