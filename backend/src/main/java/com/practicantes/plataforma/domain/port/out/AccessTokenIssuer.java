package com.practicantes.plataforma.domain.port.out;

import com.practicantes.plataforma.domain.model.AccessToken;
import com.practicantes.plataforma.domain.model.User;

public interface AccessTokenIssuer {
    AccessToken issue(User user);
}
