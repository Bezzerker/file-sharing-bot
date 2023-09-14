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
import ru.zerrbild.services.MailConfirmationService;
import ru.zerrbild.utils.ciphering.Decoder;

@RequiredArgsConstructor
@Service
public class MailConfirmationServiceImpl implements MailConfirmationService {
    @Value("${link.decryption_key}")
    private String key;
    @Value("${link.protocol}")
    private String protocol;
    @Value("${link.address}")
    private String address;
    @Value("${link.port.message_handler}")
    private String messageHandlerServicePort;
    private final Decoder decoder;
    private final UserDAO userDAO;
    private final RestTemplate restTemplate;

    @Override
    public void confirm(String encodedUserId) {
        Long userId = decoder.decodeToLong(encodedUserId, key);

        UserEntity existingUser = userDAO.findById(userId)
                .filter(user -> user.getState() == UserState.WAITING_FOR_CONFIRMATION)
                .orElseThrow(IllegalArgumentException::new);
        existingUser.setState(UserState.REGISTERED);
        userDAO.save(existingUser);

        sendConfirmationMessageToUser(existingUser);
    }

    private void sendConfirmationMessageToUser(UserEntity user) {
        restTemplate.exchange(
                String.format("%s://%s:%s/telegram/confirm", protocol, address, messageHandlerServicePort),
                HttpMethod.POST,
                new HttpEntity<>(user.getTgUserId()),
                String.class
        );
    }
}