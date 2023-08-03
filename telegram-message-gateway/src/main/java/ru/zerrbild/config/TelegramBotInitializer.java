package ru.zerrbild.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.zerrbild.controllers.TelegramBot;

@RequiredArgsConstructor
@Slf4j
@Component
public class TelegramBotInitializer {
    private final TelegramBot telegramBot;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException exception) {
            log.error("Telegram bot initialization error [{}] - Possibly an error in the wrong username or token of the bot", exception.getMessage());
        }
    }
}
