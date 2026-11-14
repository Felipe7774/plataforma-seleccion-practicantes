package com.practicantes.plataforma.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.practicantes.plataforma.application.exception.MissingStatusCommentException;
import com.practicantes.plataforma.domain.model.Applicant;
import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.model.Role;
import com.practicantes.plataforma.domain.model.StatusChange;
import com.practicantes.plataforma.domain.model.User;
import com.practicantes.plataforma.domain.port.out.ApplicantRepository;
import com.practicantes.plataforma.domain.port.out.NotificationSender;
import com.practicantes.plataforma.domain.port.out.StatusHistoryRepository;
import com.practicantes.plataforma.domain.port.out.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ChangeApplicantStatusServiceTest {

    private static final Clock FIXED_CLOCK =
            Clock.fixed(Instant.parse("2026-11-14T17:00:00Z"), ZoneOffset.UTC);

    @Mock private GetApplicantService getApplicantService;
    @Mock private ApplicantRepository applicantRepository;
    @Mock private StatusHistoryRepository statusHistoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private NotificationSender notificationSender;

    @Test
    @DisplayName("CP-29 saves the decision, status history and notification")
    void savesDecisionHistoryAndNotification() {
        ChangeApplicantStatusService service = service();
        Applicant applicant = pendingApplicant();
        User analyst = analyst();
        given(getApplicantService.get(applicant.id())).willReturn(applicant);
        given(applicantRepository.save(any(Applicant.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(userRepository.findByUsername("ana.lopez")).willReturn(Optional.of(analyst));

        Applicant updated = service.change(applicant.id(), ApplicantStatus.VIABLE, "Buen perfil", "ana.lopez");

        assertThat(updated.status()).isEqualTo(ApplicantStatus.VIABLE);
        ArgumentCaptor<StatusChange> historyCaptor = ArgumentCaptor.forClass(StatusChange.class);
        verify(statusHistoryRepository).save(historyCaptor.capture());
        assertThat(historyCaptor.getValue().previousStatus()).isEqualTo(ApplicantStatus.PENDING);
        assertThat(historyCaptor.getValue().newStatus()).isEqualTo(ApplicantStatus.VIABLE);
        assertThat(historyCaptor.getValue().comment()).isEqualTo("Buen perfil");
        assertThat(historyCaptor.getValue().changedBy()).isEqualTo(analyst.id());
        verify(notificationSender).sendStatusChanged(
                applicant.email(), applicant.fullName(), ApplicantStatus.VIABLE);
    }

    @Test
    @DisplayName("CP-30 rejects a non-viable decision without a comment")
    void rejectsNonViableDecisionWithoutComment() {
        ChangeApplicantStatusService service = service();

        assertThatThrownBy(() -> service.change(UUID.randomUUID(), ApplicantStatus.NOT_VIABLE, " ", "ana.lopez"))
                .isInstanceOf(MissingStatusCommentException.class)
                .hasMessage("Escribe el motivo de la decisión");

        verify(applicantRepository, never()).save(any(Applicant.class));
        verify(statusHistoryRepository, never()).save(any(StatusChange.class));
    }

    @Test
    @DisplayName("CP-38 keeps the decision when notification scheduling fails")
    void keepsDecisionWhenNotificationFails() {
        ChangeApplicantStatusService service = service();
        Applicant applicant = pendingApplicant();
        given(getApplicantService.get(applicant.id())).willReturn(applicant);
        given(applicantRepository.save(any(Applicant.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(userRepository.findByUsername("ana.lopez")).willReturn(Optional.of(analyst()));
        doThrow(new IllegalStateException("SMTP unavailable"))
                .when(notificationSender)
                .sendStatusChanged(any(), any(), any());

        Applicant updated = service.change(applicant.id(), ApplicantStatus.VIABLE, null, "ana.lopez");

        assertThat(updated.status()).isEqualTo(ApplicantStatus.VIABLE);
        verify(statusHistoryRepository).save(any(StatusChange.class));
    }

    private ChangeApplicantStatusService service() {
        return new ChangeApplicantStatusService(
                getApplicantService,
                applicantRepository,
                statusHistoryRepository,
                userRepository,
                notificationSender,
                FIXED_CLOCK);
    }

    private Applicant pendingApplicant() {
        return new Applicant(
                UUID.fromString("00000000-0000-0000-0000-000000000010"),
                "Laura Gómez",
                "laura@correo.com",
                "Ingeniería de Sistemas",
                8,
                "cvs/laura.pdf",
                "hoja-de-vida.pdf",
                ApplicantStatus.PENDING,
                FIXED_CLOCK.instant().minusSeconds(60),
                FIXED_CLOCK.instant().minusSeconds(60));
    }

    private User analyst() {
        return new User(
                UUID.fromString("00000000-0000-0000-0000-000000000011"),
                "ana.lopez",
                "$2a$10$hashed-password",
                Role.ANALYST,
                true,
                FIXED_CLOCK.instant());
    }
}
