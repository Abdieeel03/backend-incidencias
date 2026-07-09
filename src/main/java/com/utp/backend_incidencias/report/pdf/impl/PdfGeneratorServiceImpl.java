package com.utp.backend_incidencias.report.pdf.impl;

import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import com.utp.backend_incidencias.common.exception.ReportGenerationException;
import com.utp.backend_incidencias.incident.entity.Incident;
import com.utp.backend_incidencias.report.dto.PdfReport;
import com.utp.backend_incidencias.report.pdf.PdfGeneratorService;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.student.entity.Student;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font LABEL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
    private static final Font VALUE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 11);
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
    private static final Font TABLE_CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9);
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8);

    @Override
    public PdfReport generateStudentIncidentReport(Student student, List<Incident> incidents) {
        log.info("Generating PDF report for student: {}", student.getStudentCode());
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();
            addTitle(document, "REPORTE DE INCIDENCIAS - ESTUDIANTE");
            addStudentInfo(document, student);
            addIncidentTable(document, incidents, false);
            addFooter(document);
            document.close();

            String fileName = buildFileName("reporte_estudiante_", student.getStudentCode());
            return new PdfReport(fileName, baos.toByteArray());

        } catch (Exception e) {
            log.error("Error generating student incident report", e);
            throw new ReportGenerationException("Error al generar el reporte PDF del estudiante", e);
        }
    }

    @Override
    public PdfReport generateSchoolClassIncidentReport(SchoolClass schoolClass, List<Incident> incidents) {
        log.info("Generating PDF report for class: {}", schoolClass.getId());
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();
            addTitle(document, "REPORTE DE INCIDENCIAS - CLASE");
            addClassInfo(document, schoolClass);
            addIncidentTable(document, incidents, true);
            addFooter(document);
            document.close();

            String fileName = buildFileName("reporte_clase_", schoolClass.getName().replaceAll("\\s+", "_"));
            return new PdfReport(fileName, baos.toByteArray());

        } catch (Exception e) {
            log.error("Error generating class incident report", e);
            throw new ReportGenerationException("Error al generar el reporte PDF de la clase", e);
        }
    }

    private void addTitle(Document document, String titleText) throws DocumentException {
        Paragraph title = new Paragraph(titleText, TITLE_FONT);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20f);
        document.add(title);
    }

    private void addStudentInfo(Document document, Student student) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20f);
        try {
            table.setWidths(new float[] { 1.5f, 3f });
        } catch (DocumentException ignored) {
        }

        addInfoRow(table, "Nombre:", student.getFirstName() + " " + student.getLastName());
        addInfoRow(table, "DNI:", student.getDni());
        addInfoRow(table, "Código:", student.getStudentCode());

        document.add(table);
    }

    private void addClassInfo(Document document, SchoolClass schoolClass) throws DocumentException {
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingAfter(20f);
        try {
            table.setWidths(new float[] { 1.5f, 3f });
        } catch (DocumentException ignored) {
        }

        String teacherName = "No asignado";
        if (schoolClass.getTeacher() != null) {
            teacherName = schoolClass.getTeacher().getName();
        }

        addInfoRow(table, "Nombre del Profesor:", teacherName);
        addInfoRow(table, "Nombre de la Clase:", schoolClass.getName());

        document.add(table);
    }

    private void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, LABEL_FONT));
        labelCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, VALUE_FONT));
        valueCell.setBorder(Rectangle.NO_BORDER);
        table.addCell(valueCell);
    }

    private void addIncidentTable(Document document, List<Incident> incidents, boolean includeStudentColumn)
            throws DocumentException {
        if (incidents == null || incidents.isEmpty()) {
            Paragraph emptyMsg = new Paragraph("No se encontraron incidencias registradas.", VALUE_FONT);
            emptyMsg.setSpacingAfter(20f);
            document.add(emptyMsg);
            return;
        }

        int numColumns = includeStudentColumn ? 6 : 5;
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100);

        try {
            if (includeStudentColumn) {
                table.setWidths(new float[] { 0.5f, 1.5f, 2.5f, 1f, 1.5f, 1f });
            } else {
                table.setWidths(new float[] { 0.5f, 1.5f, 3f, 1f, 1f });
            }
        } catch (DocumentException ignored) {
        }

        // Headers
        addTableHeader(table, "N°");
        addTableHeader(table, "Título");
        addTableHeader(table, "Descripción");
        addTableHeader(table, "Estado");
        if (includeStudentColumn) {
            addTableHeader(table, "Estudiante");
        }
        addTableHeader(table, "Fecha");

        // Rows
        int count = 1;
        for (Incident incident : incidents) {
            addTableCell(table, String.valueOf(count++));
            addTableCell(table, incident.getTitle());
            addTableCell(table, incident.getDescription());
            addTableCell(table, incident.getStatus().name());
            if (includeStudentColumn) {
                String studentName = incident.getStudent().getFirstName() + " " + incident.getStudent().getLastName();
                addTableCell(table, studentName);
            }
            addTableCell(table,
                    incident.getIncidentDate() != null ? incident.getIncidentDate().format(DATE_FORMATTER) : "");
        }

        document.add(table);
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(Color.LIGHT_GRAY);
        header.setPadding(5);
        header.setPhrase(new Phrase(headerTitle, TABLE_HEADER_FONT));
        table.addCell(header);
    }

    private void addTableCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", TABLE_CELL_FONT));
        cell.setPadding(4);
        table.addCell(cell);
    }

    private void addFooter(Document document) throws DocumentException {
        String footerText = "Generado el: " + LocalDateTime.now().format(DATE_FORMATTER);
        Paragraph footer = new Paragraph(footerText, FOOTER_FONT);
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(30f);
        document.add(footer);
    }

    private String buildFileName(String prefix, String identifier) {
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMATTER);
        return prefix + identifier + "_" + timestamp + ".pdf";
    }
}
