package com.practicantes.plataforma.domain.model;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ApplicantTest {

    @Test
    @DisplayName("CP-04 rejects a semester outside the allowed range")
    void rejectsSemesterOutsideAllowedRange() {
        assertThatThrownBy(() -> applicantWithSemester(13))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("semester must be between 1 and 12");
    }

    @Test
    @DisplayName("CP-02 rejects a blank full name")
    void rejectsBlankFullName() {
        Instant now = Instant.now();

        assertThatThrownBy(
                        () -> new Applicant(
                                UUID.randomUUID(),
                                " ",
                                "laura@example.com",
                                "Ingeniería de Sistemas",
                                8,
                                "cv/uuid.pdf",
                                "hoja-de-vida.pdf",
                                ApplicantStatus.PENDING,
                                now,
                                now))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("fullName is required");
    }

    private Applicant applicantWithSemester(int semester) {
        Instant now = Instant.now();
        return new Applicant(
                UUID.randomUUID(),
                "Laura Gómez",
                "laura@example.com",
                "Ingeniería de Sistemas",
                semester,
                "cv/uuid.pdf",
                "hoja-de-vida.pdf",
                ApplicantStatus.PENDING,
                now,
                now);
    }
}
