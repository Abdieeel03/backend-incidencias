package com.utp.backend_incidencias.report.service;

import com.utp.backend_incidencias.report.dto.PdfReport;

public interface ReportService {

    PdfReport generateStudentIncidentReport(String studentCode);

    PdfReport generateClassIncidentReport(Long classId);
}
