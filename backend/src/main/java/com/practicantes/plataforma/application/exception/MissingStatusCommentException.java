package com.practicantes.plataforma.application.exception;

public class MissingStatusCommentException extends RuntimeException {
    public MissingStatusCommentException() {
        super("Escribe el motivo de la decisión");
    }
}
