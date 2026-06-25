package com.utp.backend_incidencias.incident.mapper;

import com.utp.backend_incidencias.incident.dto.request.CreateIncidentRequest;
import com.utp.backend_incidencias.incident.dto.request.UpdateIncidentRequest;
import com.utp.backend_incidencias.incident.dto.response.IncidentResponse;
import com.utp.backend_incidencias.incident.entity.Incident;

public class IncidentMapper {

    private IncidentMapper() {
    }

    public static Incident toEntity(CreateIncidentRequest req) {

        return Incident.builder()
                .title(req.getTitle())
                .description(req.getDescription())
                .build();
    }

    public static IncidentResponse toResponse(Incident incident) {

        return IncidentResponse.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .status(incident.getStatus())
                .incidentDate(incident.getIncidentDate())

                .studentId(incident.getStudent().getId())
                .studentName(
                        incident.getStudent().getFirstName() + " " +
                                incident.getStudent().getLastName()
                )

                .classId(incident.getSchoolClass().getId())
                .className(incident.getSchoolClass().getName())

                .teacherId(incident.getTeacher().getId())
                .teacherName(incident.getTeacher().getName())

                .build();
    }

    public static void updateEntity(
            Incident incident,
            UpdateIncidentRequest req
    ) {

        incident.setTitle(req.getTitle());
        incident.setDescription(req.getDescription());
    }
}
