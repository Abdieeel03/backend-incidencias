package com.utp.backend_incidencias.schoolclass.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolClassResponse {

    private Long id;

    private String name;

    private Long teacherId;

    private String teacherName;

    private List<StudentSummary> students;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudentSummary {

        private Long id;

        private String fullName;

        private String studentCode;
    }
}