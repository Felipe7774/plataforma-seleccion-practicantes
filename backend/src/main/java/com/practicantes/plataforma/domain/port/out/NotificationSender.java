package com.practicantes.plataforma.domain.port.out;

import com.practicantes.plataforma.domain.model.ApplicantStatus;

public interface NotificationSender {
    void sendStatusChanged(String email, String applicantName, ApplicantStatus status);
}
