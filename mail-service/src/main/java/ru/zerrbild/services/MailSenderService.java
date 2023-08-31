package ru.zerrbild.services;

import ru.zerrbild.utils.mail.EmailAddresseeData;

public interface MailSenderService {
    void sendEmail(EmailAddresseeData addresseeData);
}
