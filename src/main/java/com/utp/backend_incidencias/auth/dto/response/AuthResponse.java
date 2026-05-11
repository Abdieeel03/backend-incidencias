package com.utp.backend_incidencias.auth.dto.response;

import com.utp.backend_incidencias.user.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {

    private String accessToken;

    private String tokenType;

    private long expiresIn;

    private Long userId;

    private String username;

    private String email;

    private Role role;
}