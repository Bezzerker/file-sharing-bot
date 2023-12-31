package ru.zerrbild.services.message.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.zerrbild.dao.AnalysisDataDAO;
import ru.zerrbild.dao.UserDAO;
import ru.zerrbild.entities.AnalysisDataEntity;
import ru.zerrbild.entities.UserEntity;
import ru.zerrbild.entities.enums.UserState;
import ru.zerrbild.exceptions.FileDownloadException;
import ru.zerrbild.exceptions.JsonKeyNotFoundException;
import ru.zerrbild.exceptions.ResourceNotFoundException;
import ru.zerrbild.services.data.DatabaseFileLoaderService;
import ru.zerrbild.services.data.UserRegistrationManagementService;
import ru.zerrbild.services.message.LinkCreatorService;
import ru.zerrbild.services.message.MessageManagerService;
import ru.zerrbild.services.message.NotificationService;
import ru.zerrbild.services.message.enums.LinkType;
import ru.zerrbild.services.message.enums.MainCommand;

@RequiredArgsConstructor
@Slf4j
@Service
public class MessageManagerServiceImpl implements MessageManagerService {
    @Value("${telegram.bot.admin_id}")
    private Long telegramBotAdminId;
    private final NotificationService notificationService;
    private final DatabaseFileLoaderService databaseFileLoader;
    private final LinkCreatorService linkCreatorService;
    private final UserRegistrationManagementService registrationManagement;
    private final AnalysisDataDAO analysisDataDAO;
    private final UserDAO userDAO;

    @Override
    public void processTextMessage(Update update) {
        saveUpdateToDataBase(update);

        var user = findOrSaveUser(update);
        var text = update.getMessage().getText();
        var userState = user.getState();
        var messageForUser = switch (userState) {
            case UNREGISTERED -> processUnregisteredUserCommand(text, user);
            case WAITING_FOR_EMAIL -> processRegisteringUserCommand(text, user);
            case WAITING_FOR_CONFIRMATION -> processUnconfirmedUserCommand(text, user);
            case REGISTERED -> processRegisteredUserCommand(text, user);
        };
        
        notificationService.notifyUser(update, messageForUser);
    }

    @Override
    public void processDocumentMessage(Update update) {
        saveUpdateToDataBase(update);
        if (isNotAllowedSaveFile(update)) return;
        var message = createDownloadLinkMessage(update, LinkType.DOC);
        notificationService.replyToUser(update, message);
    }

    @Override
    public void processImageMessage(Update update) {
        saveUpdateToDataBase(update);
        if (isNotAllowedSaveFile(update)) return;
        var message = createDownloadLinkMessage(update, LinkType.IMAGE);
        notificationService.replyToUser(update, message);
    }

    private String createDownloadLinkMessage(Update update, LinkType linkType) {
        try {
            var fileId = (linkType == LinkType.DOC)
                    ? databaseFileLoader.loadDocument(update).getId()
                    : databaseFileLoader.loadImage(update).getId();
            var downloadLink = linkCreatorService.createDownloadLink(fileId, linkType);
            return String.format("Ваша ссылка на загрузку %s: %s", linkType.getReplyInfo(), downloadLink);
        } catch (FileDownloadException | JsonKeyNotFoundException e) {
            log.error(e.getMessage());
            return  "Сожалеем, произошла внутренняя ошибка!";
        } catch (ResourceNotFoundException e) {
            log.error(e.getMessage());
            return  "<b>Размер файла слишком большой!</b>\nФайл не должен превышать 20 Мб!";
        }
    }

    private boolean isNotAllowedSaveFile(Update update) {
        var user = findOrSaveUser(update);
        var userState = user.getState();
        boolean isNotRegistered = !userState.equals(UserState.REGISTERED);
        if (isNotRegistered) {
            String message = "Вы еще не зарегистрированы в боте, чтобы пользоваться его функциями. Выполните команду /help или зарегистрируйтесь!";
            notificationService.notifyUser(update, message);
        }
        return isNotRegistered;
    }

    private String processUnregisteredUserCommand(String receivedText, UserEntity user) {
        var command = MainCommand.findCommand(receivedText);
        return switch (command) {
            case HELP -> """
                    <b>Список доступных команд:</b>
                    /register — Регистрация пользователя в боте
                    /help — Просмотр списка доступных команд""";
            case START -> String.format("""
                    Добро пожаловать, %s!
                    
                    Перед началом работы с ботом необходимо <b>зарегистрироваться</b>, привязав свою электронную почту — /register
                    
                    Чтобы посмотреть <i>список доступных команд</i>, выполните /help""",
                    user.getFirstName());
            case REGISTER -> {
                    user.setState(UserState.WAITING_FOR_EMAIL);
                    userDAO.save(user);
                    yield "<b>Отправьте Вашу электронную почту!</b>\nЕсли хотите отменить регистрацию, выполните /cancel";
            }
            default -> """
                    Недопустимая <i>команда</i> или <i>сообщение</i>!
                    <b>Выполните регистрацию</b> для работы с ботом — /register
                    """;
        };
    }

    private String processRegisteringUserCommand(String receivedText, UserEntity user) {
        var command = MainCommand.findCommand(receivedText);
        return switch (command) {
            case HELP -> """
                    <b>Список доступных команд:</b>
                    /cancel — Отмена процедуры регистрации в боте
                    /help — Просмотр списка доступных команд""";
            case START -> String.format("""
                    Добро пожаловать, %s!
                    <b>Введите Вашу почту</b> или отмените регистрацию — /cancel""", user.getFirstName());
            case CANCEL -> registrationManagement.deregister(user);
            default -> registrationManagement.sendConfirmationLink(user, receivedText);
        };
    }

    private String processUnconfirmedUserCommand(String receivedText, UserEntity user) {
        var command = MainCommand.findCommand(receivedText);
        return switch (command) {
            case HELP -> """
                    <b>Список доступных команд:</b>
                    /cancel — Отмена подтверждения почты
                    /help — Просмотр списка доступных команд""";
            case START -> String.format("""
                    Добро пожаловать, %s!
                    <b>Подтвердите Вашу электронную почту</b> или отмените регистрацию — /cancel""", user.getFirstName());
            case CANCEL -> registrationManagement.deregister(user);
            default -> "В данный момент требуется <b>подтверждение электронной почты</b>, работа бота ограничена!";
        };
    }

    private String processRegisteredUserCommand(String receivedText, UserEntity user) {
        var command = MainCommand.findCommand(receivedText);
        return switch (command) {
            case HELP -> """
                    <b>Список доступных команд:</b>
                    /reset — Сброс регистрации и выход из бота
                    /help — Просмотр списка доступных команд""";
            case START -> String.format("""
                    Добро пожаловать, %s!
                    Вы <i>зарегистрированы</i> в боте и <b>можете загружать</b> ваши файлы!""", user.getFirstName());
            case RESET -> registrationManagement.resetRegistration(user);
            default -> "<b>Недопустимая команда!</b>\nВыполните /help для просмотра доступных команд.";
        };
    }

    private UserEntity findOrSaveUser(Update update) {
        var messageFromUpdate = update.getMessage();
        var user = messageFromUpdate.getFrom();

        var existingUserEntity = userDAO.findByTgUserId(user.getId());
        return existingUserEntity.orElseGet(() -> {
            var userState = UserState.UNREGISTERED;
            var receivedUserId = user.getId();
            if (receivedUserId.equals(telegramBotAdminId)) {
                userState = UserState.REGISTERED;
                notificationService.notifyUser(
                        user.getId(),
                        "<b>Вы создатель бота</b> — регистрация не требуется!"
                );
            }

            UserEntity transientUserEntity = UserEntity.builder()
                    .tgUserId(user.getId())
                    .username(user.getUserName())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .state(userState)
                    .build();

            return userDAO.save(transientUserEntity);
        });
    }

    private void saveUpdateToDataBase(Update update) {
        var analysisData = AnalysisDataEntity.builder()
                .update(update)
                .build();
        analysisDataDAO.save(analysisData);
    }
}