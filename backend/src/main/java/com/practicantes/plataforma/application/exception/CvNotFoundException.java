package com.practicantes.plataforma.application.exception;

public class CvNotFoundException extends RuntimeException {

    public CvNotFoundException() {
        super("La hoja de vida no está disponible");
    }
}
