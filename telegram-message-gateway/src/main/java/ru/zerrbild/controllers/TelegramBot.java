package ru.zerrbild.controllers;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Controller
public class TelegramBot extends AbilityBot {
    @Value("${bot.creator_user_id}")
    private long creatorId;
    private final UpdateProcessor updateProcessor;

    protected TelegramBot(@Value("${bot.token}") String botToken,
                          @Value("${bot.username}") String botUsername,
                          UpdateProcessor updateProcessor) {
        super(botToken, botUsername);
        this.updateProcessor = updateProcessor;
    }

    @PostConstruct
    public void initUpdateProcessor() {
        updateProcessor.setTelegramBot(this);
    }

    @Override
    public long creatorId() {
        return creatorId;
    }

    @Override
    public void onUpdateReceived(Update update) {
        updateProcessor.processUpdate(update);
    }
}
