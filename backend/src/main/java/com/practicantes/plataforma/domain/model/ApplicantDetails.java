package com.practicantes.plataforma.domain.model;

import java.util.List;

public record ApplicantDetails(Applicant applicant, List<StatusChangeWithUser> history) {}
