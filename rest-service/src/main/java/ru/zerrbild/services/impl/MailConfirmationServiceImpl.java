package ru.zerrbild.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
    private final Decoder decoder;
    private final UserDAO userDAO;

    @Override
    public void confirm(String encodedUserId) {
        Long userId = decoder.decodeToLong(encodedUserId, key);

        UserEntity existingUser = userDAO.findById(userId)
                .filter(user -> user.getState() == UserState.WAITING_FOR_CONFIRMATION)
                .orElseThrow(IllegalArgumentException::new);
        existingUser.setState(UserState.REGISTERED);
        userDAO.save(existingUser);
    }
}
