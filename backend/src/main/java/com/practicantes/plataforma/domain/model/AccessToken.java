package com.practicantes.plataforma.domain.model;

import java.time.Instant;

public record AccessToken(String value, Instant expiresAt) {}
