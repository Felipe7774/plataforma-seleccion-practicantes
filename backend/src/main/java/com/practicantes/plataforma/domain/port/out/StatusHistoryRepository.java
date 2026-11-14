package com.practicantes.plataforma.domain.port.out;

import com.practicantes.plataforma.domain.model.StatusChange;
import java.util.List;
import java.util.UUID;

public interface StatusHistoryRepository {
    StatusChange save(StatusChange statusChange);

    List<StatusChange> findByApplicantIdOrderByChangedAt(UUID applicantId);
}
