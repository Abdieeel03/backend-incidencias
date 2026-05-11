package com.utp.backend_incidencias.incident.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateIncidentRequest {

    @NotBlank
    @Size(max = 150)
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private Long studentId;

    @NotNull
    private Long classId;

    @NotNull
    private Long teacherId;
}