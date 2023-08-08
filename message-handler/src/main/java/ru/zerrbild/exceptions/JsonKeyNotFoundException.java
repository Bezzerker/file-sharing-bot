package ru.zerrbild.exceptions;

public class JsonKeyNotFoundException extends RuntimeException {
    public JsonKeyNotFoundException(String message) {
        super(message);
    }

    public JsonKeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
