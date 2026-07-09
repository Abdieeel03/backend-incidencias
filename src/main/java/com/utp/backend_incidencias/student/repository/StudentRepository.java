package com.utp.backend_incidencias.student.repository;

import com.utp.backend_incidencias.student.entity.Student;
import com.utp.backend_incidencias.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByIdAndIsDeletedFalse(Long id);

    Optional<Student> findByStudentCode(String studentCode);

    List<Student> findAllByIsDeletedFalse();

    List<Student> findAllByIsDeletedTrue();

    List<Student> findAllByCreatedByAndIsDeletedFalse(User createdBy);

    List<Student> findAllByCreatedByAndIsDeletedTrue(User createdBy);

    List<Student> findAllByParentIdAndIsDeletedFalse(Long parentId);

    List<Student> findAllByParentAndIsDeletedFalse(User parent);

    @Query("""
        SELECT s
        FROM Student s
        WHERE s.isDeleted = false
        AND (
            LOWER(CONCAT(s.firstName, ' ', s.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
            OR LOWER(s.lastName) LIKE LOWER(CONCAT('%', :query, '%'))
            OR s.dni LIKE CONCAT('%', :query, '%')
            OR s.studentCode LIKE CONCAT('%', :query, '%')
        )
    """)
    List<Student> searchStudents(String query);

    List<Student> findByIdInAndCreatedByAndIsDeletedFalse(List<Long> ids, User createdBy);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Long id);

    boolean existsByStudentCode(String studentCode);
}
