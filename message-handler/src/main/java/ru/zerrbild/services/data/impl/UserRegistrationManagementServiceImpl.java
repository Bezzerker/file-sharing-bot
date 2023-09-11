package ru.zerrbild.services.data.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.zerrbild.dao.UserDAO;
import ru.zerrbild.entities.UserEntity;
import ru.zerrbild.entities.enums.UserState;
import ru.zerrbild.services.data.UserRegistrationManagementService;
import ru.zerrbild.utils.ciphering.Encoder;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectKeyException;
import ru.zerrbild.utils.mail.EmailAddresseeData;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserRegistrationManagementServiceImpl implements UserRegistrationManagementService {
    private final Encoder encoder;
    private final RestTemplate restTemplate;
    private final UserDAO userDAO;
    @Value("${link.encryption_key}")
    private String encryptionKey;
    @Value("${link.protocol}")
    String protocol;
    @Value("${link.address}")
    String address;
    @Value("${link.port.mail_service}")
    String mailServicePort;
    @Value("${rabbitmq.exchange.user.name}")
    String userExchange;
    @Value("${rabbitmq.exchange.user.routing_key.to_registrants_queue}")
    String registrantsQueueRoutingKey;

    @Override
    public String sendConfirmationLink(UserEntity user, String userEmail) {
        var emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(userEmail)) return "<b>Указан неверный email.</b>\n" +
                "Повторите попытку или введите /cancel для отмены регистрации!";
        if (determineEmailUniqueness(userEmail)) return "Такой email уже используется другим человеком";

        try {
            var addresseeData = setAddresseeData(user, userEmail);
            var response = sendRequestToMailService(addresseeData);

            if (response.getStatusCode().isSameCodeAs(HttpStatus.OK)) {
                setUserEmailAndWaitingState(user, userEmail);
                return response.getBody();
            }

            log.error("The confirmation link was not sent. The response status was returned - {}", response.getStatusCode());
            return "<b>Отправка письма на Ваш электронный адрес не удалась</b>";
        } catch (IncorrectKeyException keyException) {
            return "Произошла внутренняя ошибка!";
        } catch (RuntimeException exception) {
            log.error("Mail Service is down. Request fails");
            return "Извините, сервис временно недоступен!";
        }
    }

    @Override
    public String deregister(UserEntity user) {
        user.setState(UserState.UNREGISTERED);
        user.setEmail(null);
        userDAO.save(user);
        return "<b>Процедура регистрации отменена!</b>\nВыполните /help для просмотра доступных команд.";
    }

    @Override
    public String resetRegistration(UserEntity userEntity) {
        userDAO.delete(userEntity);
        return "<b>Регистрация успешно сброшена!</b>\nЧтобы снова использовать бот, введите команду /start";
    }

    private ResponseEntity<String> sendRequestToMailService(EmailAddresseeData addresseeData) {
        return restTemplate.exchange(
                String.format("%s://%s:%s/mail/send", protocol, address, mailServicePort),
                HttpMethod.POST,
                new HttpEntity<>(addresseeData),
                String.class
        );
    }

    private EmailAddresseeData setAddresseeData(UserEntity user, String userEmail) {
        String encodedIdForUrl = encoder.encodeForUrl(user.getId(), encryptionKey);
        return EmailAddresseeData.builder()
                .id(encodedIdForUrl)
                .firstName(user.getFirstName())
                .email(userEmail)
                .build();
    }

    private void setUserEmailAndWaitingState(UserEntity user, String userEmail) {
        user.setState(UserState.WAITING_FOR_CONFIRMATION);
        user.setEmail(userEmail);
        userDAO.save(user);
    }

    private boolean determineEmailUniqueness(String email) {
        var existingUser = userDAO.findByEmail(email);
        return existingUser.isPresent();
    }
}
