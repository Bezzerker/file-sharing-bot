package ru.zerrbild.services;

public interface MailConfirmationService {
    void confirm(String encodedUserId);
}