package com.utp.backend_incidencias.report.controller;

import com.utp.backend_incidencias.report.dto.PdfReport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    @GetMapping(
            value = "/classes/{studentCode}/incidents",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> generateStudentIncidentReport(
            @PathVariable Long studentCode
    ) {

        PdfReport pdf = null;

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + pdf.fileName() + "\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.content());
    }

    @GetMapping(
            value = "/classes/{classId}/incidents",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<byte[]> generateClassIncidentReport(
            @PathVariable Long classId
    ) {

        PdfReport pdf = null;

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + pdf.fileName() + "\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf.content());
    }
}
