package com.utp.backend_incidencias.student.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentDetailResponse {

    private Long id;

    private String fullName;

    private String studentCode;

    private List<ClassSummary> classes;

    private List<IncidentSummary> incidents;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClassSummary {
        private Long id;
        private String name;
        private String teacher_name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class IncidentSummary {
        private Long id;
        private String title;
        private String schoolClass_name;
        private String status;
    }
}
