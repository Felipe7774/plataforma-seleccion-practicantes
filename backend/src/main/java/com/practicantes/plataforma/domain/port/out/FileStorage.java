package com.practicantes.plataforma.domain.port.out;

import com.practicantes.plataforma.domain.model.CvFile;
import com.practicantes.plataforma.domain.model.StoredFile;

public interface FileStorage {
    StoredFile save(CvFile file);
}

