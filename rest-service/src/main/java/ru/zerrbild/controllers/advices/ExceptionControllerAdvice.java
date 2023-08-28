package ru.zerrbild.controllers.advices;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectCiphertextException;
import ru.zerrbild.utils.ciphering.exceptions.IncorrectKeyException;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    @ExceptionHandler(IncorrectCiphertextException.class)
    public ResponseEntity<String> handleIncorrectCipherTextException() {
        return ResponseEntity.badRequest().body("Неверный id сообщения");
    }

    @ExceptionHandler(IncorrectKeyException.class)
    public ResponseEntity<String> handleIncorrectKeyException() {
        return ResponseEntity.internalServerError().body("Ключ расшифровки сообщений поврежден или не валиден");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleFileNotFoundException() {
        return ResponseEntity.internalServerError().body("Файл не был найден");
    }
}