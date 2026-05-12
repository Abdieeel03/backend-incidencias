package com.utp.backend_incidencias.student.service;

import com.utp.backend_incidencias.student.dto.request.CreateStudentRequest;
import com.utp.backend_incidencias.student.dto.request.UpdateStudentRequest;
import com.utp.backend_incidencias.student.dto.response.StudentResponse;

import java.util.List;

public interface StudentService {

    StudentResponse createStudent(CreateStudentRequest req);

    StudentResponse getStudentById(Long id);

    List<StudentResponse> getAllStudents();

    List<StudentResponse> getDeletedStudents();

    List<StudentResponse> getStudentsByParentId(Long parentId);

    List<StudentResponse> searchStudents(String query);

    StudentResponse updateStudent(Long id, UpdateStudentRequest req);

    void deleteStudent(Long id);

    void restoreStudent(Long id);
}
