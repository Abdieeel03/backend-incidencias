package com.utp.backend_incidencias.incident.controller;

import com.utp.backend_incidencias.common.constants.SuccessMessages;
import com.utp.backend_incidencias.common.response.ApiResponse;
import com.utp.backend_incidencias.incident.dto.request.CreateIncidentRequest;
import com.utp.backend_incidencias.incident.dto.request.UpdateIncidentRequest;
import com.utp.backend_incidencias.incident.dto.response.IncidentResponse;
import com.utp.backend_incidencias.incident.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @PreAuthorize("hasRole('PROFESOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<IncidentResponse>> createIncident(
            @Valid @RequestBody CreateIncidentRequest req
    ) {

        IncidentResponse res = incidentService.createIncident(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<IncidentResponse>builder()
                                .success(true)
                                .message(SuccessMessages.INCIDENT_CREATED)
                                .data(res)
                                .build()
                );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentResponse>> getIncidentById(
            @PathVariable Long id
    ) {

        IncidentResponse res = incidentService.getIncidentById(id);

        return ResponseEntity.ok(
                ApiResponse.<IncidentResponse>builder()
                        .success(true)
                        .message(SuccessMessages.INCIDENT_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<IncidentResponse>>> getAllIncidents() {

        List<IncidentResponse> res = incidentService.getAllIncidents();

        return ResponseEntity.ok(
                ApiResponse.<List<IncidentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.INCIDENTS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('COORDINADOR', 'PROFESOR')")
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<IncidentResponse>>> getDeletedIncidents() {

        List<IncidentResponse> res = incidentService.getDeletedIncidents();

        return ResponseEntity.ok(
                ApiResponse.<List<IncidentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.DELETED_INCIDENTS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @GetMapping("/my-incidents")
    public ResponseEntity<ApiResponse<List<IncidentResponse>>> getMyIncidents() {

        List<IncidentResponse> res = incidentService.getMyIncidents();

        return ResponseEntity.ok(
                ApiResponse.<List<IncidentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.INCIDENTS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<IncidentResponse>>> getIncidentsByStudent(
            @PathVariable Long studentId
    ) {

        List<IncidentResponse> res =
                incidentService.getIncidentsByStudent(studentId);

        return ResponseEntity.ok(
                ApiResponse.<List<IncidentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.INCIDENTS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize(" hasAnyRole('ADMIN','COORDINADOR','PROFESOR')")
    @GetMapping("/class/{classId}")
    public ResponseEntity<ApiResponse<List<IncidentResponse>>> getIncidentsByClass(
            @PathVariable Long classId
    ) {

        List<IncidentResponse> res =
                incidentService.getIncidentsByClass(classId);

        return ResponseEntity.ok(
                ApiResponse.<List<IncidentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.INCIDENTS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IncidentResponse>> updateIncident(
            @PathVariable Long id,
            @Valid @RequestBody UpdateIncidentRequest req
    ) {

        IncidentResponse res =
                incidentService.updateIncident(id, req);

        return ResponseEntity.ok(
                ApiResponse.<IncidentResponse>builder()
                        .success(true)
                        .message(SuccessMessages.INCIDENT_UPDATED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("hasRole('PADRE')")
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id
    ) {

        incidentService.markAsRead(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(SuccessMessages.INCIDENT_MARKED_AS_READ)
                        .build()
        );
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteIncident(
            @PathVariable Long id
    ) {

        incidentService.deleteIncident(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(SuccessMessages.INCIDENT_DELETED)
                        .build()
        );
    }

    @PreAuthorize("hasRole('PROFESOR')")
    @PutMapping("/restore/{id}")
    public ResponseEntity<ApiResponse<Void>> restoreIncident(
            @PathVariable Long id
    ) {

        incidentService.restoreIncident(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(SuccessMessages.INCIDENT_RESTORED)
                        .build()
        );
    }
}