package ru.osipov.cloudstorage.exceptions;

public class RepositoryException extends RuntimeException{
    public RepositoryException(String msg) {
        super(msg);
    }
}