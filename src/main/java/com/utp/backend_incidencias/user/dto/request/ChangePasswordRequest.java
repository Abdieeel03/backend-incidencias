package com.utp.backend_incidencias.user.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {

    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 6, max = 100)
    private String newPassword;

    @NotBlank
    private String confirmPassword;
}
