package ru.zerrbild.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.zerrbild.dao.UserDAO;
import ru.zerrbild.entities.UserEntity;
import ru.zerrbild.entities.enums.UserState;
import ru.zerrbild.exceptions.RegisteredUserNotFoundException;
import ru.zerrbild.services.MailConfirmationService;
import ru.zerrbild.utils.ciphering.Decoder;

@RequiredArgsConstructor
@Service
public class MailConfirmationServiceImpl implements MailConfirmationService {
    @Value("${ciphering.key}")
    private String decodingKey;
    @Value("${url_components.message_handler.domain}")
    private String messageHandlerDomain;
    @Value("${url_components.message_handler.port}")
    private String messageHandlerServicePort;
    private final Decoder decoder;
    private final UserDAO userDAO;
    private final RestTemplate restTemplate;

    @Override
    public void confirm(String encodedUserId) {
        Long userId = decoder.decodeToLong(encodedUserId, decodingKey);

        UserEntity existingUser = userDAO.findById(userId)
                .filter(user -> user.getState() == UserState.WAITING_FOR_CONFIRMATION)
                .orElseThrow(() -> new RegisteredUserNotFoundException(
                        String.format("User with id = '%s' has already been registered or is missing in the database", userId)));
        existingUser.setState(UserState.REGISTERED);
        userDAO.save(existingUser);

        sendConfirmationMessageToUser(existingUser);
    }

    private void sendConfirmationMessageToUser(UserEntity user) {
        restTemplate.exchange(
                String.format("http://%s:%s/notifications/registration/complete", messageHandlerDomain, messageHandlerServicePort),
                HttpMethod.POST,
                new HttpEntity<>(user.getTgUserId()),
                String.class
        );
    }
}