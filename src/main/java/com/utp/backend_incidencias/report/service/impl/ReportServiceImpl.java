package com.utp.backend_incidencias.report.service.impl;

import com.utp.backend_incidencias.report.dto.PdfReport;
import com.utp.backend_incidencias.report.service.ReportService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    @Override
    public PdfReport generateStudentIncidentReport(String studentCode) {

        //TODO: Obtener datos del alumno
        //TODO: Generar PDF con OpenPDF
        //TODO: Construir el nombre del archivo

        throw new UnsupportedOperationException("Not implement yet.");
    }

    @Override
    public PdfReport generateClassIncidentReport(Long classId) {

        //TODO: Obtener datos del alumno
        //TODO: Generar PDF con OpenPDF
        //TODO: Construir el nombre del archivo

        throw new UnsupportedOperationException("Not implement yet.");
    }
}
