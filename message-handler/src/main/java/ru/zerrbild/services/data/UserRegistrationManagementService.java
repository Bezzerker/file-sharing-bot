package ru.zerrbild.services.data;

import ru.zerrbild.entities.UserEntity;

public interface UserRegistrationManagementService {
    String sendConfirmationLink(UserEntity user, String userEmail);
    String deregister(UserEntity user);
    void deregister(Long userId);
    String resetRegistration(UserEntity user);
}