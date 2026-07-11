package com.utp.backend_incidencias.report.pdf;

import com.utp.backend_incidencias.incident.entity.Incident;
import com.utp.backend_incidencias.report.dto.PdfReport;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.student.entity.Student;

import java.util.List;

public interface PdfGeneratorService {

    PdfReport generateStudentIncidentReport(
            Student student,
            List<Incident> incidents
    );

    PdfReport generateSchoolClassIncidentReport(
            SchoolClass schoolClass,
            List<Incident> incidents
    );
}
