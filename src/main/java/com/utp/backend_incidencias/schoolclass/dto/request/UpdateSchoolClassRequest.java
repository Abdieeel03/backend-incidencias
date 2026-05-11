package com.utp.backend_incidencias.schoolclass.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateSchoolClassRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotNull
    private Long teacherId;

    private List<Long> studentIds;
}