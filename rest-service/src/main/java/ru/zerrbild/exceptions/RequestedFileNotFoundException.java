package ru.zerrbild.exceptions;

public class RequestedFileNotFoundException extends RuntimeException {
    public RequestedFileNotFoundException(String message) {
        super(message);
    }
}