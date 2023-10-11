package ru.zerrbild.controllers.advices;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zerrbild.exceptions.RegisteredUserNotFoundException;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectCiphertextException;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectKeyException;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = {IncorrectCiphertextException.class, NoSuchElementException.class})
    public ResponseEntity<String> handleInvalidAddressExceptions() {
        return ResponseEntity.badRequest().body("Требуемый ресурс не найден!");
    }

    @ExceptionHandler(IncorrectKeyException.class)
    public ResponseEntity<String> handleIncorrectKeyException() {
        return ResponseEntity.internalServerError().body("Упс, произошла ошибка! Подождите, пока разработчики всё исправят!");
    }

    @ExceptionHandler(RegisteredUserNotFoundException.class)
    public ResponseEntity<String> handleRegisteredUserNotFoundException(RuntimeException exception) {
        log.info(exception.getMessage());
        return ResponseEntity.badRequest().body("Ссылка недействительна!");
    }
}