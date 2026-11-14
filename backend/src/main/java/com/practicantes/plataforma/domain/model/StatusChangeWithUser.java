package com.practicantes.plataforma.domain.model;

public record StatusChangeWithUser(StatusChange statusChange, String changedByUsername) {}
