package com.practicantes.plataforma.application.exception;

public class TooManyRequestsException extends RuntimeException {
    public TooManyRequestsException() {
        super("Demasiados intentos. Intenta de nuevo en un minuto");
    }
}
