package com.practicantes.plataforma.application.exception;

public class DuplicateApplicantEmailException extends RuntimeException {
    public DuplicateApplicantEmailException() {
        super("Este correo ya está registrado");
    }
}

