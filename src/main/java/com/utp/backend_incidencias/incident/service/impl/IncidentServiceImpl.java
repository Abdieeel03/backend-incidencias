package com.utp.backend_incidencias.incident.service.impl;

import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ConflictException;
import com.utp.backend_incidencias.common.exception.ForbiddenException;
import com.utp.backend_incidencias.common.exception.ResourceNotFoundException;
import com.utp.backend_incidencias.incident.dto.request.CreateIncidentRequest;
import com.utp.backend_incidencias.incident.dto.request.UpdateIncidentRequest;
import com.utp.backend_incidencias.incident.dto.response.IncidentResponse;
import com.utp.backend_incidencias.incident.entity.Incident;
import com.utp.backend_incidencias.incident.enums.IncidentStatus;
import com.utp.backend_incidencias.incident.mapper.IncidentMapper;
import com.utp.backend_incidencias.incident.repository.IncidentRepository;
import com.utp.backend_incidencias.incident.service.IncidentService;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.schoolclass.repository.SchoolClassRepository;
import com.utp.backend_incidencias.security.service.OwnershipService;
import com.utp.backend_incidencias.security.service.SecurityService;
import com.utp.backend_incidencias.student.entity.Student;
import com.utp.backend_incidencias.student.repository.StudentRepository;
import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService {

    private final IncidentRepository incidentRepository;
    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final SecurityService securityService;
    private final OwnershipService ownershipService;

    @Override
    public IncidentResponse createIncident(CreateIncidentRequest req) {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() != Role.PROFESOR) {
            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }

        Student student = findStudentById(req.getStudentId());

        SchoolClass schoolClass = findClassById(req.getClassId());

        ownershipService.validateTeacherAssignment(schoolClass);

        validateStudentInClass(student, schoolClass);

        Incident incident = IncidentMapper.toEntity(req);

        incident.setStudent(student);
        incident.setSchoolClass(schoolClass);
        incident.setTeacher(currentUser);

        Incident savedIncident = incidentRepository.save(incident);

        log.info(
                "Incidente creado correctamente con id: {}",
                savedIncident.getId()
        );

        return IncidentMapper.toResponse(savedIncident);
    }

    @Override
    public IncidentResponse getIncidentById(Long id) {

        Incident incident = findIncidentById(id);

        ownershipService.validateIncidentAccess(incident);

        return IncidentMapper.toResponse(incident);
    }

    @Override
    public List<IncidentResponse> getAllIncidents() {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {

            return incidentRepository.findAllByIsDeletedFalse()
                    .stream()
                    .map(IncidentMapper::toResponse)
                    .toList();
        }

        if (currentUser.getRole() == Role.COORDINADOR) {

            return incidentRepository.findAllByIsDeletedFalse()
                    .stream()
                    .filter(incident ->
                            incident.getTeacher() != null &&
                                    incident.getTeacher().getCreatedBy() != null &&
                                    incident.getTeacher().getCreatedBy()
                                            .getId()
                                            .equals(currentUser.getId())
                    )
                    .map(IncidentMapper::toResponse)
                    .toList();
        }

        if (currentUser.getRole() == Role.PROFESOR) {

            return incidentRepository
                    .findAllByTeacherAndIsDeletedFalse(currentUser)
                    .stream()
                    .map(IncidentMapper::toResponse)
                    .toList();
        }

        if (currentUser.getRole() == Role.PADRE) {

            return incidentRepository.findAllByIsDeletedFalse()
                    .stream()
                    .filter(incident ->
                            incident.getStudent() != null &&
                                    incident.getStudent().getParent() != null &&
                                    incident.getStudent().getParent()
                                            .getId()
                                            .equals(currentUser.getId())
                    )
                    .map(IncidentMapper::toResponse)
                    .toList();
        }

        throw new ForbiddenException(
                ErrorMessages.FORBIDDEN_ACCESS
        );
    }

    @Override
    public List<IncidentResponse> getDeletedIncidents() {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {

            return incidentRepository.findAllByIsDeletedTrue()
                    .stream()
                    .map(IncidentMapper::toResponse)
                    .toList();
        }

        if (currentUser.getRole() == Role.COORDINADOR) {

            return incidentRepository.findAllByIsDeletedTrue()
                    .stream()
                    .filter(incident ->
                            incident.getTeacher() != null &&
                                    incident.getTeacher().getCreatedBy() != null &&
                                    incident.getTeacher().getCreatedBy()
                                            .getId()
                                            .equals(currentUser.getId())
                    )
                    .map(IncidentMapper::toResponse)
                    .toList();
        }

        if (currentUser.getRole() == Role.PROFESOR) {

            return incidentRepository
                    .findAllByTeacherAndIsDeletedTrue(currentUser)
                    .stream()
                    .map(IncidentMapper::toResponse)
                    .toList();
        }

        throw new ForbiddenException(
                ErrorMessages.FORBIDDEN_ACCESS
        );
    }

    @Override
    public List<IncidentResponse> getMyIncidents() {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() != Role.PROFESOR) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }

        return incidentRepository
                .findAllByTeacherAndIsDeletedFalse(currentUser)
                .stream()
                .map(IncidentMapper::toResponse)
                .toList();
    }

    @Override
    public List<IncidentResponse> getIncidentsByStudent(Long studentId) {

        Student student = findStudentById(studentId);

        ownershipService.validateStudentAccess(student);

        return incidentRepository
                .findAllByStudentAndIsDeletedFalse(student)
                .stream()
                .map(IncidentMapper::toResponse)
                .toList();
    }

    @Override
    public List<IncidentResponse> getIncidentsByClass(Long classId) {

        SchoolClass schoolClass = findClassById(classId);

        ownershipService.validateClassAccess(schoolClass);

        return incidentRepository
                .findAllBySchoolClassAndIsDeletedFalse(schoolClass)
                .stream()
                .map(IncidentMapper::toResponse)
                .toList();
    }

    @Override
    public IncidentResponse updateIncident(
            Long id,
            UpdateIncidentRequest req
    ) {

        Incident incident = findIncidentById(id);

        ownershipService.validateTeacherIncidentAccess(incident);

        Student student = findStudentById(req.getStudentId());

        SchoolClass schoolClass = findClassById(req.getClassId());

        ownershipService.validateTeacherAssignment(schoolClass);

        validateStudentInClass(student, schoolClass);

        IncidentMapper.updateEntity(incident, req);

        incident.setStudent(student);
        incident.setSchoolClass(schoolClass);

        Incident updatedIncident = incidentRepository.save(incident);

        log.info(
                "Incidente actualizado correctamente con id: {}",
                updatedIncident.getId()
        );

        return IncidentMapper.toResponse(updatedIncident);
    }

    @Override
    public void markAsRead(Long id) {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() != Role.PADRE) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }

        Incident incident = findIncidentById(id);

        ownershipService.validateParentIncidentAccess(incident);

        incident.setStatus(IncidentStatus.LEIDA);

        incidentRepository.save(incident);

        log.info(
                "Incidente marcado como leído con id: {}",
                incident.getId()
        );
    }

    @Override
    public void deleteIncident(Long id) {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() != Role.PROFESOR) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }

        Incident incident = findIncidentById(id);

        ownershipService.validateTeacherIncidentAccess(incident);

        incident.setIsDeleted(true);

        incidentRepository.save(incident);

        log.info(
                "Incidente eliminado correctamente con id: {}",
                incident.getId()
        );
    }

    @Override
    public void restoreIncident(Long id) {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() != Role.PROFESOR) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }

        Incident incident = incidentRepository
                .findByIdAndIsDeletedTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorMessages.INCIDENT_NOT_FOUND
                        )
                );

        ownershipService.validateTeacherIncidentAccess(incident);

        incident.setIsDeleted(false);

        incidentRepository.save(incident);

        log.info(
                "Incidente restaurado correctamente con id: {}",
                incident.getId()
        );
    }

    private Incident findIncidentById(Long id) {

        return incidentRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorMessages.INCIDENT_NOT_FOUND
                        )
                );
    }

    private Student findStudentById(Long id) {

        return studentRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorMessages.STUDENT_NOT_FOUND
                        )
                );
    }

    private SchoolClass findClassById(Long id) {

        return schoolClassRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorMessages.CLASS_NOT_FOUND
                        )
                );
    }

    private void validateStudentInClass(
            Student student,
            SchoolClass schoolClass
    ) {

        boolean exists = schoolClass.getStudents()
                .stream()
                .anyMatch(s ->
                        s.getId().equals(student.getId())
                );

        if (!exists) {

            throw new ConflictException(
                    ErrorMessages.STUDENT_NOT_IN_CLASS
            );
        }
    }
}