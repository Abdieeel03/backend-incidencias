package com.utp.backend_incidencias.schoolclass.controller;

import com.utp.backend_incidencias.common.constants.SuccessMessages;
import com.utp.backend_incidencias.common.response.ApiResponse;
import com.utp.backend_incidencias.schoolclass.dto.request.AddStudentsRequest;
import com.utp.backend_incidencias.schoolclass.dto.request.CreateSchoolClassRequest;
import com.utp.backend_incidencias.schoolclass.dto.request.UpdateSchoolClassRequest;
import com.utp.backend_incidencias.schoolclass.dto.response.SchoolClassResponse;
import com.utp.backend_incidencias.schoolclass.service.SchoolClassService;
import com.utp.backend_incidencias.student.dto.response.StudentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class SchoolClassController {

    private final SchoolClassService schoolClassService;

    @PreAuthorize("hasRole('COORDINADOR')")
    @PostMapping
    public ResponseEntity<ApiResponse<SchoolClassResponse>> createClass(
            @Valid @RequestBody CreateSchoolClassRequest req
    ) {

        SchoolClassResponse res = schoolClassService.createClass(req);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<SchoolClassResponse>builder()
                        .success(true)
                        .message(SuccessMessages.CLASS_CREATED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR', 'PROFESOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SchoolClassResponse>> getClassById(
            @PathVariable Long id
    ) {

        SchoolClassResponse res = schoolClassService.getClassById(id);

        return ResponseEntity.ok(
                ApiResponse.<SchoolClassResponse>builder()
                        .success(true)
                        .message(SuccessMessages.CLASS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'COORDINADOR')")
    @GetMapping
    public ResponseEntity<ApiResponse<List<SchoolClassResponse>>> getAllClasses() {

        List<SchoolClassResponse> res = schoolClassService.getAllClasses();

        return ResponseEntity.ok(
                ApiResponse.<List<SchoolClassResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.CLASSES_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("hasRole('COORDINADOR')")
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<SchoolClassResponse>>> getDeletedClasses() {

        List<SchoolClassResponse> res = schoolClassService.getDeletedClasses();

        return ResponseEntity.ok(
                ApiResponse.<List<SchoolClassResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.DELETED_CLASSES_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @GetMapping("/my-classes")
    @PreAuthorize("hasRole('PROFESOR')")
    public ResponseEntity<ApiResponse<List<SchoolClassResponse>>> getMyClasses() {

        List<SchoolClassResponse> res = schoolClassService.getMyClasses();

        return ResponseEntity.ok(
                ApiResponse.<List<SchoolClassResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.CLASSES_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("hasAnyRole('ADMIN','COORDINADOR','PROFESOR')")
    @GetMapping("/{id}/students")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsByClass(
            @PathVariable Long id
    ) {

        List<StudentResponse> res = schoolClassService.getStudentsByClass(id);

        return ResponseEntity.ok(
                ApiResponse.<List<StudentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENTS_RETRIEVED_BY_CLASS)
                        .data(res)
                        .build()
        );
    }

    @PutMapping("/{id}/students")
    @PreAuthorize("hasRole('COORDINADOR')")
    public ResponseEntity<ApiResponse<SchoolClassResponse>> addStudentsToClass(
            @PathVariable Long id,
            @Valid @RequestBody AddStudentsRequest req
    ) {

        SchoolClassResponse res = schoolClassService.addStudentsToClass(
                id,
                req.getStudentIds()
        );

        return ResponseEntity.ok(
                ApiResponse.<SchoolClassResponse>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENTS_ADDED_TO_CLASS)
                        .data(res)
                        .build()
        );
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COORDINADOR')")
    public ResponseEntity<ApiResponse<SchoolClassResponse>> updateClass(
            @PathVariable Long id,
            @Valid @RequestBody UpdateSchoolClassRequest req
    ) {

        SchoolClassResponse res = schoolClassService.updateClass(id, req);

        return ResponseEntity.ok(
                ApiResponse.<SchoolClassResponse>builder()
                        .success(true)
                        .message(SuccessMessages.CLASS_UPDATED)
                        .data(res)
                        .build()
        );
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COORDINADOR')")
    public ResponseEntity<ApiResponse<Void>> deleteClass(@PathVariable Long id) {

        schoolClassService.deleteClass(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(SuccessMessages.CLASS_DELETED)
                        .build()
        );
    }

    @PutMapping("/restore/{id}")
    @PreAuthorize("hasRole('COORDINADOR')")
    public ResponseEntity<ApiResponse<Void>> restoreClass(@PathVariable Long id) {

        schoolClassService.restoreClass(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(SuccessMessages.CLASS_RESTORED)
                        .build()
        );
    }
}
