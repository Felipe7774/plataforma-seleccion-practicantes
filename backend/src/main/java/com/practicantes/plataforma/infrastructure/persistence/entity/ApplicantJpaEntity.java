package com.practicantes.plataforma.infrastructure.persistence.entity;

import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "applicants")
public class ApplicantJpaEntity {

    @Id
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, length = 200)
    private String career;

    @Column(nullable = false)
    private short semester;

    @Column(name = "cv_key", nullable = false, length = 255)
    private String cvKey;

    @Column(name = "cv_original_name", nullable = false, length = 255)
    private String cvOriginalName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicantStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ApplicantJpaEntity() {}

    private ApplicantJpaEntity(Applicant applicant) {
        this.id = applicant.id();
        this.fullName = applicant.fullName();
        this.email = applicant.email();
        this.career = applicant.career();
        this.semester = (short) applicant.semester();
        this.cvKey = applicant.cvKey();
        this.cvOriginalName = applicant.cvOriginalName();
        this.status = applicant.status();
        this.createdAt = applicant.createdAt();
        this.updatedAt = applicant.updatedAt();
    }

    public static ApplicantJpaEntity fromDomain(Applicant applicant) {
        return new ApplicantJpaEntity(applicant);
    }

    public Applicant toDomain() {
        return new Applicant(
                id,
                fullName,
                email,
                career,
                semester,
                cvKey,
                cvOriginalName,
                status,
                createdAt,
                updatedAt);
    }
}

