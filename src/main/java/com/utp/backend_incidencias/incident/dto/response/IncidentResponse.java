package com.utp.backend_incidencias.incident.dto.response;

import com.utp.backend_incidencias.incident.enums.IncidentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentResponse {

    private Long id;

    private String title;

    private String description;

    private IncidentStatus status;

    private LocalDateTime incidentDate;

    private Long studentId;

    private String studentName;

    private Long classId;

    private String className;

    private Long teacherId;

    private String teacherName;
}