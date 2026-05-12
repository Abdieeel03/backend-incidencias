package com.utp.backend_incidencias.schoolclass.repository;

import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SchoolClassRepository extends JpaRepository<SchoolClass, Long> {

    Optional<SchoolClass> findByIdAndIsDeletedFalse(Long id);

    Optional<SchoolClass> findByIdAndIsDeletedTrue(Long id);

    List<SchoolClass> findAllByCreatedByAndIsDeletedFalse(User createdBy);

    List<SchoolClass> findAllByCreatedByAndIsDeletedTrue(User createdBy);

    List<SchoolClass> findAllByTeacherAndIsDeletedFalse(User teacher);

    List<SchoolClass> findAllByIsDeletedFalse();

    List<SchoolClass> findAllByIsDeletedTrue();
}
