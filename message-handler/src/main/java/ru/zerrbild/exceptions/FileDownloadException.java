package ru.zerrbild.exceptions;

public class FileDownloadException extends RuntimeException {
    public FileDownloadException(String message, Throwable cause) {
        super(message, cause);
    }
}
