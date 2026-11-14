package com.practicantes.plataforma.domain.model;

import java.util.Arrays;

public record CvFile(String originalFilename, String contentType, byte[] content) {

    public CvFile {
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new IllegalArgumentException("originalFilename is required");
        }
        content = content == null ? new byte[0] : Arrays.copyOf(content, content.length);
    }

    @Override
    public byte[] content() {
        return Arrays.copyOf(content, content.length);
    }
}

