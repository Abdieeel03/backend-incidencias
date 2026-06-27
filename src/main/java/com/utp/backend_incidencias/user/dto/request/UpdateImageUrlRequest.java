package com.utp.backend_incidencias.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateImageUrlRequest {

    @NotBlank(message = "La URL de la imagen es obligatoria")
    private String imageUrl;
}
