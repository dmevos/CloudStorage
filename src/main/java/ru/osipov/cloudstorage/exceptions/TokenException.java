package ru.osipov.cloudstorage.exceptions;

public class TokenException extends RuntimeException {
    public TokenException(String msg) {
        super(msg);
    }
}