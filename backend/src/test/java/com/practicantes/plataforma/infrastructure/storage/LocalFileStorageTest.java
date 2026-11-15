package com.practicantes.plataforma.infrastructure.storage;

import static org.assertj.core.api.Assertions.assertThat;

import com.practicantes.plataforma.domain.model.CvFile;
import com.practicantes.plataforma.domain.model.StoredFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalFileStorageTest {

    @Test
    void savesAndLoadsPdfContent(@TempDir Path temporaryDirectory) {
        LocalFileStorage storage = new LocalFileStorage(temporaryDirectory.toString());
        byte[] expected = "%PDF-1.7\n".getBytes(StandardCharsets.US_ASCII);

        StoredFile stored = storage.save(new CvFile("cv.pdf", "application/pdf", expected));

        assertThat(storage.load(stored.key())).isPresent();
        assertThat(storage.load(stored.key()).orElseThrow()).containsExactly(expected);
    }

    @Test
    void returnsEmptyWhenStoredFileDoesNotExist(@TempDir Path temporaryDirectory) {
        LocalFileStorage storage = new LocalFileStorage(temporaryDirectory.toString());

        assertThat(storage.load("missing.pdf")).isEmpty();
    }
}
