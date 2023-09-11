package ru.zerrbild.services.data;

import ru.zerrbild.entities.UserEntity;

public interface UserRegistrationManagementService {
    String sendConfirmationLink(UserEntity user, String userEmail);
    String deregister(UserEntity user);
    String resetRegistration(UserEntity user);
}
