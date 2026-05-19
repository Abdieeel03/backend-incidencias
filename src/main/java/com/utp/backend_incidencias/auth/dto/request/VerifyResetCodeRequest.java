package com.utp.backend_incidencias.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerifyResetCodeRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String code;
}
