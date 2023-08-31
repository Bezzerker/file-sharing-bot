package ru.zerrbild.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zerrbild.services.MailSenderService;
import ru.zerrbild.utils.mail.EmailAddresseeData;

@RequiredArgsConstructor
@RequestMapping("/mail")
@RestController
public class MailController {
    private final MailSenderService mailSenderService;

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody EmailAddresseeData addresseeData) {
        mailSenderService.sendEmail(addresseeData);
        return ResponseEntity.ok("<b>Сообщение с ссылкой подтверждения было отправлено на почту!</b>\n" +
                                            "Перейдите по ссылке в письме для завершения регистрации!");
    }
}