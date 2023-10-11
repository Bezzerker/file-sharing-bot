package ru.zerrbild.exceptions;

public class RegisteredUserNotFoundException extends RuntimeException {
    public RegisteredUserNotFoundException(String message) {
        super(message);
    }
}