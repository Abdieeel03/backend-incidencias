package com.utp.backend_incidencias.student.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String dni;

    private String studentCode;

    private Long parentId;

    private String parentName;
}