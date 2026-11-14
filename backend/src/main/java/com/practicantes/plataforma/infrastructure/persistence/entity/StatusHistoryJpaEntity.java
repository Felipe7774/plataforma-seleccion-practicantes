package com.practicantes.plataforma.infrastructure.persistence.entity;

import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.model.StatusChange;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "status_history")
public class StatusHistoryJpaEntity {

    @Id
    private UUID id;

    @Column(name = "applicant_id", nullable = false)
    private UUID applicantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 20)
    private ApplicantStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 20)
    private ApplicantStatus newStatus;

    @Column(length = 1000)
    private String comment;

    @Column(name = "changed_by", nullable = false)
    private UUID changedBy;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    protected StatusHistoryJpaEntity() {}

    private StatusHistoryJpaEntity(StatusChange statusChange) {
        this.id = statusChange.id();
        this.applicantId = statusChange.applicantId();
        this.previousStatus = statusChange.previousStatus();
        this.newStatus = statusChange.newStatus();
        this.comment = statusChange.comment();
        this.changedBy = statusChange.changedBy();
        this.changedAt = statusChange.changedAt();
    }

    public static StatusHistoryJpaEntity fromDomain(StatusChange statusChange) {
        return new StatusHistoryJpaEntity(statusChange);
    }

    public StatusChange toDomain() {
        return new StatusChange(
                id, applicantId, previousStatus, newStatus, comment, changedBy, changedAt);
    }
}

