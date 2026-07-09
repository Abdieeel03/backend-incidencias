package com.utp.backend_incidencias.report.service.impl;

import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ResourceNotFoundException;
import com.utp.backend_incidencias.incident.entity.Incident;
import com.utp.backend_incidencias.incident.repository.IncidentRepository;
import com.utp.backend_incidencias.report.dto.PdfReport;
import com.utp.backend_incidencias.report.pdf.PdfGeneratorService;
import com.utp.backend_incidencias.report.service.ReportService;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.schoolclass.repository.SchoolClassRepository;
import com.utp.backend_incidencias.security.service.OwnershipService;
import com.utp.backend_incidencias.student.entity.Student;
import com.utp.backend_incidencias.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final StudentRepository studentRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final IncidentRepository incidentRepository;
    private final OwnershipService ownershipService;
    private final PdfGeneratorService pdfGeneratorService;

    @Override
    public PdfReport generateStudentIncidentReport(String studentCode) {

        Student student = studentRepository.findByStudentCode(studentCode)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.STUDENT_NOT_FOUND));

        ownershipService.validateStudentAccess(student);

        List<Incident> incidents = incidentRepository.findAllByStudent(student);

        log.info("Generating Student Incident Report for Student {}", studentCode);

        return pdfGeneratorService.generateStudentIncidentReport(student, incidents);
    }

    @Override
    public PdfReport generateClassIncidentReport(Long classId) {

        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.CLASS_NOT_FOUND));

        ownershipService.validateClassAccess(schoolClass);

        List<Incident> incidents = incidentRepository.findAllBySchoolClass(schoolClass);

        return pdfGeneratorService.generateSchoolClassIncidentReport(schoolClass, incidents);
    }
}
