package ru.osipov.cloudstorage.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.osipov.cloudstorage.exceptions.*;
import ru.osipov.cloudstorage.models.Error;

@RestControllerAdvice
@Slf4j
public class AllExceptionHandlers {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Error> authExceptionHandler(Exception e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new Error(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<Error> tokenExceptionHandler(TokenException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new Error(e.getMessage(), HttpStatus.UNAUTHORIZED.value()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InputDataException.class)
    public ResponseEntity<Error> inputDataExceptionHandler(InputDataException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new Error(e.getMessage(), HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<Error> repositoryExceptionHandler(RepositoryException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new Error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<Error> fileExceptionHandler(FileException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(new Error(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}