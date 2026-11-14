package com.practicantes.plataforma.application.exception;

public class InvalidApplicantStatusException extends RuntimeException {
    public InvalidApplicantStatusException() {
        super("Solo se puede decidir viable o no viable");
    }
}

