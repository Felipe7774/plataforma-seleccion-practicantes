package com.practicantes.plataforma.application.exception;

import java.util.UUID;

public class ApplicantNotFoundException extends RuntimeException {
    public ApplicantNotFoundException(UUID applicantId) {
        super("No encontramos a este practicante: " + applicantId);
    }
}

