package com.utp.backend_incidencias.report.dto;

public record PdfReport(
        String fileName,
        byte[] content
) {
}
