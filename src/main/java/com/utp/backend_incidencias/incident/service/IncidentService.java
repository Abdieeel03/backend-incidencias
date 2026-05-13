package com.utp.backend_incidencias.incident.service;

import com.utp.backend_incidencias.incident.dto.request.CreateIncidentRequest;
import com.utp.backend_incidencias.incident.dto.request.UpdateIncidentRequest;
import com.utp.backend_incidencias.incident.dto.response.IncidentResponse;

import java.util.List;

public interface IncidentService {

    IncidentResponse createIncident(CreateIncidentRequest req);

    IncidentResponse getIncidentById(Long id);

    List<IncidentResponse> getAllIncidents();

    List<IncidentResponse> getDeletedIncidents();

    List<IncidentResponse> getMyIncidents();

    List<IncidentResponse> getIncidentsByStudent(Long studentId);

    List<IncidentResponse> getIncidentsByClass(Long classId);

    IncidentResponse updateIncident(Long id, UpdateIncidentRequest req);

    void markAsRead(Long id);

    void deleteIncident(Long id);

    void restoreIncident(Long id);
}
