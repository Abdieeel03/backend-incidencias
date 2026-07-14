package com.utp.backend_incidencias.incident.repository;

import com.utp.backend_incidencias.incident.entity.Incident;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.student.entity.Student;
import com.utp.backend_incidencias.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Optional<Incident> findByIdAndIsDeletedFalse(Long id);

    Optional<Incident> findByIdAndIsDeletedTrue(Long id);

    List<Incident> findAllByIsDeletedFalse();

    List<Incident> findAllByIsDeletedTrue();

    List<Incident> findAllByTeacherAndIsDeletedFalse(User teacher);

    List<Incident> findAllByTeacherAndIsDeletedTrue(User teacher);

    List<Incident> findAllByStudentAndIsDeletedFalse(Student student);

    List<Incident> findAllBySchoolClassAndIsDeletedFalse(SchoolClass schoolClass);

    List<Incident> findAllByStudent(Student student);

    List<Incident> findAllBySchoolClass(SchoolClass schoolClass);
}
