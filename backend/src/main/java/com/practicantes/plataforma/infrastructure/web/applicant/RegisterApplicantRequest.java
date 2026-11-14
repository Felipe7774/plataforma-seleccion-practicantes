package com.practicantes.plataforma.infrastructure.web.applicant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public record RegisterApplicantRequest(
        @NotBlank(message = "El nombre completo es obligatorio") String fullName,
        @NotBlank(message = "El correo electrónico es obligatorio")
                @Email(message = "Ingresa un correo válido")
                String email,
        @NotBlank(message = "La carrera universitaria es obligatoria") String career,
        @Min(value = 1, message = "El semestre debe estar entre 1 y 12")
                @Max(value = 12, message = "El semestre debe estar entre 1 y 12")
                int semester,
        MultipartFile cv) {}

