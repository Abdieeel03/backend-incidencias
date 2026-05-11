package com.utp.backend_incidencias.student.mapper;

import com.utp.backend_incidencias.student.dto.request.CreateStudentRequest;
import com.utp.backend_incidencias.student.dto.request.UpdateStudentRequest;
import com.utp.backend_incidencias.student.dto.response.StudentResponse;
import com.utp.backend_incidencias.student.entity.Student;

public class StudentMapper {

    private StudentMapper() {
    }

    public static Student toEntity(CreateStudentRequest req) {

        return Student.builder()
                .firstName(req.getFirstName())
                .lastName(req.getLastName())
                .dni(req.getDni())
                .build();
    }

    public static StudentResponse toResponse(Student student) {

        return StudentResponse.builder()
                .id(student.getId())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .dni(student.getDni())
                .studentCode(student.getStudentCode())
                .parentId(student.getParent().getId())
                .parentName(student.getParent().getName())
                .build();
    }

    public static void updateEntity(Student student, UpdateStudentRequest req) {

        student.setFirstName(req.getFirstName());
        student.setLastName(req.getLastName());
        student.setDni(req.getDni());
    }
}