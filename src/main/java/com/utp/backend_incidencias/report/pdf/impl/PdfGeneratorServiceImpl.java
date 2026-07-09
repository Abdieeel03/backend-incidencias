package com.utp.backend_incidencias.report.pdf.impl;

import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import com.utp.backend_incidencias.common.exception.ReportGenerationException;
import com.utp.backend_incidencias.incident.entity.Incident;
import com.utp.backend_incidencias.incident.enums.IncidentStatus;
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

    // Paleta de colores corporativa
    private static final Color PRIMARY_COLOR = new Color(41, 65, 122); // Azul oscuro
    private static final Color PRIMARY_LIGHT = new Color(52, 86, 153); // Azul medio
    private static final Color ACCENT_COLOR = new Color(70, 130, 180); // Steel blue
    private static final Color ROW_EVEN = new Color(235, 241, 250); // Azul muy claro (filas pares)
    private static final Color ROW_ODD = Color.WHITE; // Blanco (filas impares)
    private static final Color SEPARATOR_COLOR = new Color(180, 200, 230); // Azul grisáceo para separadores
    private static final Color LABEL_BG = new Color(225, 235, 248); // Fondo para labels de info
    private static final Color STATUS_LEIDA = new Color(39, 174, 96); // Verde para leída
    private static final Color STATUS_NO_LEIDA = new Color(231, 76, 60); // Rojo para no leída
    private static final Color FOOTER_COLOR = new Color(130, 145, 170); // Gris azulado

    // Fuentes con colores
    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.WHITE);
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.WHITE);
    private static final Font LABEL_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, PRIMARY_COLOR);
    private static final Font VALUE_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(50, 50, 50));
    private static final Font TABLE_HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
    private static final Font TABLE_CELL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 9, new Color(60, 60, 60));
    private static final Font FOOTER_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 8, FOOTER_COLOR);
    private static final Font SECTION_TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, PRIMARY_COLOR);
    private static final Font EMPTY_FONT = FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10,
            new Color(150, 150, 150));

    @Override
    public PdfReport generateStudentIncidentReport(Student student, List<Incident> incidents) {
        log.info("Generating PDF report for student: {}", student.getStudentCode());
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, baos);

            document.open();
            addTitleBanner(document, "REPORTE DE INCIDENCIAS", "Estudiante");
            addSeparator(document);
            addSectionTitle(document, "Información del Estudiante");
            addStudentInfo(document, student);
            addSeparator(document);
            addSectionTitle(document, "Registro de Incidencias");
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

            Document document = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(document, baos);

            document.open();
            addTitleBanner(document, "REPORTE DE INCIDENCIAS", "Clase");
            addSeparator(document);
            addSectionTitle(document, "Información de la Clase");
            addClassInfo(document, schoolClass);
            addSeparator(document);
            addSectionTitle(document, "Registro de Incidencias");
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

    // ========================= SECCIÓN: ENCABEZADO =========================

    private void addTitleBanner(Document document, String titleText, String subtitle) throws DocumentException {
        // Tabla que actúa como banner con fondo de color
        PdfPTable banner = new PdfPTable(1);
        banner.setWidthPercentage(100);
        banner.setSpacingAfter(15f);

        PdfPCell bannerCell = new PdfPCell();
        bannerCell.setBackgroundColor(PRIMARY_COLOR);
        bannerCell.setPadding(20);
        bannerCell.setBorder(Rectangle.NO_BORDER);
        bannerCell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph titleParagraph = new Paragraph(titleText, TITLE_FONT);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        bannerCell.addElement(titleParagraph);

        Paragraph subtitleParagraph = new Paragraph(subtitle, SUBTITLE_FONT);
        subtitleParagraph.setAlignment(Element.ALIGN_CENTER);
        subtitleParagraph.setSpacingBefore(4f);
        bannerCell.addElement(subtitleParagraph);

        banner.addCell(bannerCell);
        document.add(banner);
    }

    private void addSectionTitle(Document document, String sectionText) throws DocumentException {
        Paragraph section = new Paragraph(sectionText, SECTION_TITLE_FONT);
        section.setSpacingBefore(8f);
        section.setSpacingAfter(8f);
        document.add(section);
    }

    private void addSeparator(Document document) throws DocumentException {
        PdfPTable separator = new PdfPTable(1);
        separator.setWidthPercentage(100);
        separator.setSpacingBefore(5f);
        separator.setSpacingAfter(5f);

        PdfPCell line = new PdfPCell();
        line.setBorder(Rectangle.BOTTOM);
        line.setBorderColor(SEPARATOR_COLOR);
        line.setBorderWidth(1.5f);
        line.setFixedHeight(2f);
        separator.addCell(line);

        document.add(separator);
    }

    // ========================= SECCIÓN: INFORMACIÓN =========================

    private void addStudentInfo(Document document, Student student) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10f);
        try {
            table.setWidths(new float[] { 1.2f, 2.8f, 1.2f, 2.8f });
        } catch (DocumentException ignored) {
        }

        addInfoRow(table, "Nombre", student.getFirstName() + " " + student.getLastName());
        addInfoRow(table, "DNI", student.getDni());
        addInfoRow(table, "Código", student.getStudentCode());
        // Celda vacía para completar la fila
        addInfoRow(table, "Total Incidencias", "—");

        document.add(table);
    }

    private void addClassInfo(Document document, SchoolClass schoolClass) throws DocumentException {
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setSpacingAfter(10f);
        try {
            table.setWidths(new float[] { 1.2f, 2.8f, 1.2f, 2.8f });
        } catch (DocumentException ignored) {
        }

        String teacherName = "No asignado";
        if (schoolClass.getTeacher() != null) {
            teacherName = schoolClass.getTeacher().getName();
        }

        addInfoRow(table, "Profesor", teacherName);
        addInfoRow(table, "Clase", schoolClass.getName());

        document.add(table);
    }

    private void addInfoRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, LABEL_FONT));
        labelCell.setBackgroundColor(LABEL_BG);
        labelCell.setBorder(Rectangle.BOX);
        labelCell.setBorderColor(SEPARATOR_COLOR);
        labelCell.setBorderWidth(0.5f);
        labelCell.setPadding(6);
        labelCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(labelCell);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, VALUE_FONT));
        valueCell.setBorder(Rectangle.BOX);
        valueCell.setBorderColor(SEPARATOR_COLOR);
        valueCell.setBorderWidth(0.5f);
        valueCell.setPadding(6);
        valueCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(valueCell);
    }

    // ========================= SECCIÓN: TABLA DE INCIDENCIAS
    // =========================

    private void addIncidentTable(Document document, List<Incident> incidents, boolean includeStudentColumn)
            throws DocumentException {
        if (incidents == null || incidents.isEmpty()) {
            Paragraph emptyMsg = new Paragraph("No se encontraron incidencias registradas.", EMPTY_FONT);
            emptyMsg.setAlignment(Element.ALIGN_CENTER);
            emptyMsg.setSpacingBefore(15f);
            emptyMsg.setSpacingAfter(20f);
            document.add(emptyMsg);
            return;
        }

        int numColumns = includeStudentColumn ? 6 : 5;
        PdfPTable table = new PdfPTable(numColumns);
        table.setWidthPercentage(100);
        table.setSpacingBefore(5f);

        try {
            if (includeStudentColumn) {
                table.setWidths(new float[] { 0.4f, 1.4f, 2.5f, 0.9f, 1.4f, 1f });
            } else {
                table.setWidths(new float[] { 0.4f, 1.5f, 3f, 0.9f, 1f });
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

        // Rows con colores alternados (zebra)
        int count = 1;
        for (Incident incident : incidents) {
            Color rowColor = (count % 2 == 0) ? ROW_EVEN : ROW_ODD;

            addTableCell(table, String.valueOf(count), rowColor);
            addTableCell(table, incident.getTitle(), rowColor);
            addTableCell(table, incident.getDescription(), rowColor);
            addStatusCell(table, incident.getStatus(), rowColor);
            if (includeStudentColumn) {
                String studentName = incident.getStudent().getFirstName() + " " + incident.getStudent().getLastName();
                addTableCell(table, studentName, rowColor);
            }
            addTableCell(table,
                    incident.getIncidentDate() != null ? incident.getIncidentDate().format(DATE_FORMATTER) : "",
                    rowColor);
            count++;
        }

        document.add(table);
    }

    private void addTableHeader(PdfPTable table, String headerTitle) {
        PdfPCell header = new PdfPCell();
        header.setBackgroundColor(PRIMARY_LIGHT);
        header.setPadding(7);
        header.setBorder(Rectangle.NO_BORDER);
        header.setPhrase(new Phrase(headerTitle, TABLE_HEADER_FONT));
        header.setHorizontalAlignment(Element.ALIGN_CENTER);
        header.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(header);
    }

    private void addTableCell(PdfPTable table, String text, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text != null ? text : "", TABLE_CELL_FONT));
        cell.setPaddingLeft(5);
        cell.setPaddingRight(5);
        cell.setPaddingTop(8);
        cell.setPaddingBottom(8);
        cell.setBackgroundColor(bgColor);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(SEPARATOR_COLOR);
        cell.setBorderWidth(0.5f);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    private void addStatusCell(PdfPTable table, IncidentStatus status, Color bgColor) {
        Color statusColor = (status == IncidentStatus.LEIDA) ? STATUS_LEIDA : STATUS_NO_LEIDA;
        String statusText = (status == IncidentStatus.LEIDA) ? "LEÍDA" : "NO LEÍDA";

        Font statusFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);

        // Tabla interna para simular un "badge" de estado
        PdfPTable badge = new PdfPTable(1);
        PdfPCell badgeCell = new PdfPCell(new Phrase(statusText, statusFont));
        badgeCell.setBackgroundColor(statusColor);
        badgeCell.setPadding(3);
        badgeCell.setBorder(Rectangle.NO_BORDER);
        badgeCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        badge.addCell(badgeCell);

        PdfPCell cell = new PdfPCell(badge);
        cell.setPadding(4);
        cell.setBackgroundColor(bgColor);
        cell.setBorder(Rectangle.BOTTOM);
        cell.setBorderColor(SEPARATOR_COLOR);
        cell.setBorderWidth(0.5f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(cell);
    }

    // ========================= SECCIÓN: PIE DE PÁGINA =========================

    private void addFooter(Document document) throws DocumentException {
        addSeparator(document);

        PdfPTable footerTable = new PdfPTable(2);
        footerTable.setWidthPercentage(100);
        footerTable.setSpacingBefore(5f);
        try {
            footerTable.setWidths(new float[] { 1f, 1f });
        } catch (DocumentException ignored) {
        }

        PdfPCell leftCell = new PdfPCell(new Phrase("Sistema de Gestión de Incidencias", FOOTER_FONT));
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        footerTable.addCell(leftCell);

        String dateText = "Generado el: " + LocalDateTime.now().format(DATE_FORMATTER);
        PdfPCell rightCell = new PdfPCell(new Phrase(dateText, FOOTER_FONT));
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(rightCell);

        document.add(footerTable);
    }

    // ========================= UTILIDADES =========================

    private String buildFileName(String prefix, String identifier) {
        String timestamp = LocalDateTime.now().format(FILE_DATE_FORMATTER);
        return prefix + identifier + "_" + timestamp + ".pdf";
    }
}
