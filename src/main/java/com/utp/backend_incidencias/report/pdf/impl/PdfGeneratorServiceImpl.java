package com.utp.backend_incidencias.report.pdf.impl;

import com.utp.backend_incidencias.incident.entity.Incident;
import com.utp.backend_incidencias.report.dto.PdfReport;
import com.utp.backend_incidencias.report.pdf.PdfGeneratorService;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.student.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    @Override
    public PdfReport generateStudentIncidentReport(Student student, List<Incident> incidents) {

        // TODO Crear nombre del archivo (Incluir student y date)

        // TODO Crear ByteArrayOutputStream -> recuerda que PdfReport devuelve tambien un arreglo de bytes (byte[])

        // TODO Crear Document

        // TODO Agregar título

        // TODO Agregar información del alumno

        // TODO Agregar tabla de incidencias

        // TODO Cerrar documento

        // Usar Slf4j para logs

        throw new UnsupportedOperationException("Not implement yet.");
    }

    @Override
    public PdfReport generateSchoolClassIncidentReport(SchoolClass schoolClass, List<Incident> incidents) {

        // TODO Crear nombre del archivo (Incluir clase y date)

        // TODO Crear ByteArrayOutputStream -> recuerda que PdfReport devuelve tambien un arreglo de bytes (byte[])

        // TODO Crear Document

        // TODO Agregar información de la clase

        // TODO Agregar tabla de incidencias

        // TODO Cerrar documento

        // Usar Slf4j para logs

        throw new UnsupportedOperationException("Not implement yet.");
    }
}
