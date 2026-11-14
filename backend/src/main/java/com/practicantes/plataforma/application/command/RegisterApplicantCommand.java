package com.practicantes.plataforma.application.command;

import com.practicantes.plataforma.domain.model.CvFile;

public record RegisterApplicantCommand(
        String fullName, String email, String career, int semester, CvFile cv) {}

