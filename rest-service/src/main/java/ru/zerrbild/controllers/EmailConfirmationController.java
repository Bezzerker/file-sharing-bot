package ru.zerrbild.controllers;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.zerrbild.services.MailConfirmationService;

@RequestMapping("/mail")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
public class EmailConfirmationController {
    MailConfirmationService confirmationService;

    @GetMapping("/confirmation")
    public ResponseEntity<String> confirmEmail(@RequestParam String userId) {
        confirmationService.confirm(userId);
        return ResponseEntity.ok("Вы успешно зарегистрированы!");
    }
}
