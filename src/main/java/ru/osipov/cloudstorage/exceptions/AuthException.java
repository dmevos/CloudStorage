package ru.osipov.cloudstorage.exceptions;

public class AuthException extends RuntimeException{
    public AuthException(String msg){
        super(msg);
    }
}
