package com.korm.exception;

public class IncorrectKormAnnotationException extends RuntimeException{
    public IncorrectKormAnnotationException(String message) {
        super(message);
    }
}
