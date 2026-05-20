package com.utp.backend_incidencias.student.controller;

import com.utp.backend_incidencias.common.constants.SuccessMessages;
import com.utp.backend_incidencias.common.response.ApiResponse;
import com.utp.backend_incidencias.student.dto.request.CreateStudentRequest;
import com.utp.backend_incidencias.student.dto.request.UpdateStudentRequest;
import com.utp.backend_incidencias.student.dto.response.StudentDetailResponse;
import com.utp.backend_incidencias.student.dto.response.StudentResponse;
import com.utp.backend_incidencias.student.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody CreateStudentRequest req
    ) {

        StudentResponse res = studentService.createStudent(req);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<StudentResponse>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENT_CREATED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudentById(
            @PathVariable Long id
    ) {

        StudentResponse res = studentService.getStudentById(id);

        return ResponseEntity.ok(
                ApiResponse.<StudentResponse>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENT_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getAllStudents() {

        List<StudentResponse> res = studentService.getAllStudents();

        return ResponseEntity.ok(
                ApiResponse.<List<StudentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENTS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<StudentDetailResponse>> getStudentDetails(
            @PathVariable Long id
    ) {

        StudentDetailResponse res = studentService.getStudentDetails(id);

        return ResponseEntity.ok(
                ApiResponse.<StudentDetailResponse>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENT_DETAILS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getDeletedStudents() {

        List<StudentResponse> res = studentService.getDeletedStudents();

        return ResponseEntity.ok(
                ApiResponse.<List<StudentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.DELETED_STUDENTS_FOUND)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> searchStudents(
            @RequestParam String query
    ) {

        List<StudentResponse> res = studentService.searchStudents(query);

        return ResponseEntity.ok(
                ApiResponse.<List<StudentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.SEARCH_RESULTS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @GetMapping("/parent/{parentId}")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getStudentsByParentId(
            @PathVariable Long parentId
    ) {

        List<StudentResponse> res =
                studentService.getStudentsByParentId(parentId);

        return ResponseEntity.ok(
                ApiResponse.<List<StudentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENTS_RETRIEVED_BY_PARENT)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize("hasRole('PADRE')")
    @GetMapping("/my-children")
    public ResponseEntity<ApiResponse<List<StudentResponse>>> getMyChildren() {

        List<StudentResponse> res = studentService.getStudentsByParentId(null);

        return ResponseEntity.ok(
                ApiResponse.<List<StudentResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENTS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest req
    ) {

        StudentResponse res = studentService.updateStudent(id, req);

        return ResponseEntity.ok(
                ApiResponse.<StudentResponse>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENT_UPDATED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStudent(
            @PathVariable Long id
    ) {

        studentService.deleteStudent(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENT_DELETED)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreStudent(
            @PathVariable Long id
    ) {

        studentService.restoreStudent(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(SuccessMessages.STUDENT_RESTORED)
                        .build()
        );
    }
}
