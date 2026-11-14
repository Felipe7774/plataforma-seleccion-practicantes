package com.practicantes.plataforma.domain.model;

public record StoredFile(String key, String originalFilename) {
    public StoredFile {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key is required");
        }
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("originalFilename is required");
        }
    }
}

