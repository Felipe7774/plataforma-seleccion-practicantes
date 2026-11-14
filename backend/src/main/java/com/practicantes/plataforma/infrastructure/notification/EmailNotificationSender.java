package com.practicantes.plataforma.infrastructure.notification;

import com.practicantes.plataforma.domain.model.ApplicantStatus;
import com.practicantes.plataforma.domain.port.out.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailNotificationSender implements NotificationSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailNotificationSender.class);

    private final JavaMailSender mailSender;
    private final String from;

    public EmailNotificationSender(
            JavaMailSender mailSender, @Value("${app.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    @Async
    public void sendStatusChanged(String email, String applicantName, ApplicantStatus status) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(email);
            message.setSubject("Actualización de tu postulación");
            message.setText("Hola " + applicantName + ",\n\nTu postulación ahora está: " + status + ".");
            mailSender.send(message);
        } catch (RuntimeException exception) {
            LOGGER.error("No fue posible enviar la notificación a {}", email, exception);
        }
    }
}
