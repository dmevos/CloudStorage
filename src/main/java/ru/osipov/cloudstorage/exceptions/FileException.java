package ru.osipov.cloudstorage.exceptions;

public class FileException extends RuntimeException{
    public FileException(String msg){
        super(msg);
    }
}