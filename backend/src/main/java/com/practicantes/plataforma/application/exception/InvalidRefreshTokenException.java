package com.practicantes.plataforma.application.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("La sesión no es válida o ya expiró");
    }
}
