package ru.zerrbild.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zerrbild.services.UpdateProducerService;

import static org.telegram.abilitybots.api.util.AbilityUtils.getChatId;

@Slf4j
@Controller
public class UpdateProcessor {
    @Value("${rabbitmq.exchange.name}")
    private String exchange;
    @Value("${rabbitmq.queue.text}")
    private String textQueue;
    @Value("${rabbitmq.queue.document}")
    private String documentQueue;
    @Value("${rabbitmq.queue.image}")
    private String imageQueue;
    private TelegramBot telegramBot;
    private final UpdateProducerService messageProducer;

    public UpdateProcessor(UpdateProducerService messageProducer) {
        this.messageProducer = messageProducer;
    }

    void setTelegramBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    void processUpdate(Update update) {
        if (update == null) {
            log.error("The update received is null");
            return;
        }

        if (update.hasMessage()) {
            defineAndProcessMessage(update);
        } else {
            log.error("Message not received");
        }
    }

    private void defineAndProcessMessage(Update update) {
        var message = update.getMessage();

        if (message.hasDocument()) {
            processDocumentMessage(update);
        } else if (message.hasPhoto()) {
            processImageMessage(update);
        } else if (message.hasText()) {
            processTextMessage(update);
        } else processOtherTypeOfMessage(message);
    }

    private void processTextMessage(Update update) {
        messageProducer.produce(exchange, textQueue, update);
    }

    private void processDocumentMessage(Update update) {
        telegramBot.silent().send("Получен документ — происходит обработка!", getChatId(update));
        messageProducer.produce(exchange, documentQueue, update);
    }

    private void processImageMessage(Update update) {
        telegramBot.silent().send("Получена фотография — происходит обработка!", getChatId(update));
        messageProducer.produce(exchange, imageQueue, update);
    }

    private void processOtherTypeOfMessage(Message message) {
        telegramBot.silent().send("Получен неверный тип сообщения. Убедитесь, что вы отправили текст, фотографию или документ!", message.getChatId());
    }
}
