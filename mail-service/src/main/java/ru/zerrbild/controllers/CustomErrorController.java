package ru.zerrbild.controllers;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public ResponseEntity<String> handleError(HttpServletRequest request) {
        var status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status == null) return ResponseEntity
                .internalServerError().
                body("Непредвиденная ошибка!");

        int statusCode = (Integer) status;
        var httpStatus = HttpStatus.valueOf(statusCode);
        if (httpStatus.is4xxClientError()) {
            log.info("The user followed the wrong endpoint or forgot to specify a query parameter");
            return ResponseEntity
                    .status(httpStatus)
                    .body("Требуемый ресурс не найден!");
        } else {
            log.error("Internal server error, look at the logs and run DEBUG logging mode!");
            return ResponseEntity
                    .status(httpStatus)
                    .body("Упс, произошла ошибка! Подождите, пока разработчики всё исправят!");
        }
    }
}