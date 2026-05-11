package com.utp.backend_incidencias.schoolclass.mapper;

import com.utp.backend_incidencias.schoolclass.dto.request.CreateSchoolClassRequest;
import com.utp.backend_incidencias.schoolclass.dto.request.UpdateSchoolClassRequest;
import com.utp.backend_incidencias.schoolclass.dto.response.SchoolClassResponse;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.student.entity.Student;

import java.util.Collections;
import java.util.List;

public class SchoolClassMapper {

    private SchoolClassMapper() {
    }

    public static SchoolClass toEntity(CreateSchoolClassRequest req) {

        return SchoolClass.builder()
                .name(req.getName())
                .build();
    }

    public static SchoolClassResponse toResponse(SchoolClass schoolClass) {

        List<SchoolClassResponse.StudentSummary> students =
                schoolClass.getStudents() == null
                        ? Collections.emptyList()
                        : schoolClass.getStudents()
                          .stream()
                          .map(SchoolClassMapper::mapStudentSummary)
                          .toList();

        return SchoolClassResponse.builder()
                .id(schoolClass.getId())
                .name(schoolClass.getName())

                .teacherId(schoolClass.getTeacher().getId())
                .teacherName(schoolClass.getTeacher().getName())

                .students(students)
                .build();
    }

    public static void updateEntity(
            SchoolClass schoolClass,
            UpdateSchoolClassRequest req
    ) {

        schoolClass.setName(req.getName());
    }

    private static SchoolClassResponse.StudentSummary mapStudentSummary(
            Student student
    ) {

        return SchoolClassResponse.StudentSummary.builder()
                .id(student.getId())
                .fullName(
                        student.getFirstName() + " " +
                                student.getLastName()
                )
                .studentCode(student.getStudentCode())
                .build();
    }
}