package com.utp.backend_incidencias.student.mapper;

import com.utp.backend_incidencias.incident.entity.Incident;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.student.dto.request.CreateStudentRequest;
import com.utp.backend_incidencias.student.dto.request.UpdateStudentRequest;
import com.utp.backend_incidencias.student.dto.response.StudentDetailResponse;
import com.utp.backend_incidencias.student.dto.response.StudentResponse;
import com.utp.backend_incidencias.student.entity.Student;

import java.util.List;

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

    public static StudentDetailResponse toDetailResponse(Student student) {

        return StudentDetailResponse.builder()
                .id(student.getId())
                .fullName(student.getFirstName() + " " + student.getLastName())
                .studentCode(student.getStudentCode())

                .classes(
                        student.getClasses() == null ? List.of() :
                                student.getClasses().stream()
                                .map(StudentMapper::mapClassSummary)
                                .toList()
                )

                .incidents(
                        student.getIncidents() == null ? List.of() :
                                student.getIncidents().stream()
                                .map(StudentMapper::mapIncidentSummary)
                                .toList()
                )
                .build();
    }

    public static void updateEntity(Student student, UpdateStudentRequest req) {

        student.setFirstName(req.getFirstName());
        student.setLastName(req.getLastName());
        student.setDni(req.getDni());
    }

    private static StudentDetailResponse.ClassSummary mapClassSummary(SchoolClass schoolClass) {

        return StudentDetailResponse.ClassSummary.builder()
                .id(schoolClass.getId())
                .name(schoolClass.getName())
                .teacher_name(schoolClass.getTeacher().getName())
                .build();
    }

    private static StudentDetailResponse.IncidentSummary mapIncidentSummary(Incident incident) {

        return StudentDetailResponse.IncidentSummary.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .schoolClass_name(incident.getSchoolClass().getName())
                .status(incident.getStatus().name())
                .build();
    }
}