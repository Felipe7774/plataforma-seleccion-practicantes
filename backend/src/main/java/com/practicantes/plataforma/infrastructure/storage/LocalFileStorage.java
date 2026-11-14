package com.practicantes.plataforma.infrastructure.storage;

import com.practicantes.plataforma.domain.model.CvFile;
import com.practicantes.plataforma.domain.model.StoredFile;
import com.practicantes.plataforma.domain.port.out.FileStorage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalFileStorage implements FileStorage {

    private final Path rootDirectory;

    public LocalFileStorage(@Value("${app.storage.local-path}") String localPath) {
        this.rootDirectory = Path.of(localPath).toAbsolutePath().normalize();
    }

    @Override
    public StoredFile save(CvFile file) {
        try {
            Files.createDirectories(rootDirectory);
            String key = UUID.randomUUID() + ".pdf";
            Path destination = rootDirectory.resolve(key).normalize();
            if (!destination.startsWith(rootDirectory)) {
                throw new IllegalStateException("Invalid storage destination");
            }

            Files.write(destination, file.content(), StandardOpenOption.CREATE_NEW);
            return new StoredFile(key, safeOriginalFilename(file.originalFilename()));
        } catch (IOException exception) {
            throw new IllegalStateException("No fue posible guardar la hoja de vida", exception);
        }
    }

    private String safeOriginalFilename(String originalFilename) {
        Path filename = Path.of(originalFilename).getFileName();
        if (filename == null || filename.toString().isBlank()) {
            return "hoja-de-vida.pdf";
        }
        return filename.toString();
    }
}

