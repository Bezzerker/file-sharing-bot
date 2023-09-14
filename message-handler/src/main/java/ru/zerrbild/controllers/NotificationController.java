package ru.zerrbild.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.zerrbild.services.message.NotificationService;

@RequiredArgsConstructor
@RequestMapping("/telegram")
@RestController
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping("/confirm")
    public ResponseEntity<String> notifyRegistrationCompletion(@RequestBody Long tgUserId) {
        notificationService.notifyRegistrationCompletion(tgUserId);
        return ResponseEntity.ok("Уведомление о завершении регистрации отправлено!");
    }
}
