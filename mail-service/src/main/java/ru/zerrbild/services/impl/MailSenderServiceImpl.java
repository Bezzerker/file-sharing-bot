package ru.zerrbild.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.zerrbild.services.MailSenderService;
import ru.zerrbild.utils.mail.EmailAddresseeData;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailSenderServiceImpl implements MailSenderService {
    @Value("${telegram.bot.name}")
    private String botName;
    @Value("${link.protocol}")
    private String protocol;
    @Value("${link.address}")
    private String address;
    @Value("${link.port.rest_service}")
    private String port;
    @Value("${spring.mail.username}")
    private String senderEmail;
    private final JavaMailSender mailSender;

    @Override
    public void sendEmail(EmailAddresseeData addresseeData) {
        var mailMessage = new SimpleMailMessage();
        mailMessage.setTo(addresseeData.getEmail());
        mailMessage.setFrom(senderEmail);
        mailMessage.setSubject(String.format("%s — Подтверждение электронной почты", botName));
        var confirmationLink=String.format("%s://%s:%s/mail/confirmation?userId=", protocol, address, port);

        var sentMessage = String.format("""
                        %s, перейдите по ссылке, чтобы подтвердить адрес электронной почты и завершить регистрацию в боте — %s%s

                        Внимание: если вы не подтвердите ваш email в течение часа, то процедура регистрации будет отменена!""",
                addresseeData.getFirstName(), confirmationLink, addresseeData.getId());

        mailMessage.setText(sentMessage);
        mailSender.send(mailMessage);
        log.info("Отправлено сообщение на адрес - {} | id - {} | username - {}",
                addresseeData.getEmail(), addresseeData.getId(), addresseeData.getFirstName());
    }
}