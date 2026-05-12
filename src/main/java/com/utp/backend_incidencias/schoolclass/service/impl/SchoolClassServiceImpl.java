package com.utp.backend_incidencias.schoolclass.service.impl;

import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ConflictException;
import com.utp.backend_incidencias.common.exception.ResourceNotFoundException;
import com.utp.backend_incidencias.schoolclass.dto.request.CreateSchoolClassRequest;
import com.utp.backend_incidencias.schoolclass.dto.request.UpdateSchoolClassRequest;
import com.utp.backend_incidencias.schoolclass.dto.response.SchoolClassResponse;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.schoolclass.mapper.SchoolClassMapper;
import com.utp.backend_incidencias.schoolclass.repository.SchoolClassRepository;
import com.utp.backend_incidencias.schoolclass.service.SchoolClassService;
import com.utp.backend_incidencias.security.service.OwnershipService;
import com.utp.backend_incidencias.security.service.SecurityService;
import com.utp.backend_incidencias.student.entity.Student;
import com.utp.backend_incidencias.student.repository.StudentRepository;
import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.enums.Role;
import com.utp.backend_incidencias.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchoolClassServiceImpl implements SchoolClassService {

    private final SchoolClassRepository schoolClassRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final SecurityService securityService;
    private final OwnershipService ownershipService;

    @Override
    public SchoolClassResponse createClass(CreateSchoolClassRequest req) {

        User currentUser = securityService.getCurrentUser();

        User teacher = findTeacherById(req.getTeacherId());

        ownershipService.validateUserOwnership(teacher);

        validateTeacherRole(teacher);

        List<Student> students = findStudentsByIds(req.getStudentIds());

        ownershipService.validateStudentsOwnership(students);

        SchoolClass schoolClass = SchoolClass.builder()
                .name(req.getName())
                .teacher(teacher)
                .students(students)
                .createdBy(currentUser)
                .build();

        SchoolClass savedClass = schoolClassRepository.save(schoolClass);

        log.info(
                "Clase creada correctamente con id: {}",
                savedClass.getId()
        );

        return SchoolClassMapper.toResponse(savedClass);
    }

    @Override
    public SchoolClassResponse getClassById(Long id) {

        SchoolClass schoolClass = findClassById(id);

        ownershipService.validateClassAccess(schoolClass);

        return SchoolClassMapper.toResponse(schoolClass);
    }

    @Override
    public List<SchoolClassResponse> getAllClasses() {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {

            return schoolClassRepository.findAllByIsDeletedFalse()
                    .stream()
                    .map(SchoolClassMapper::toResponse)
                    .toList();
        }

        return schoolClassRepository
                .findAllByCreatedByAndIsDeletedFalse(currentUser)
                .stream()
                .map(SchoolClassMapper::toResponse)
                .toList();
    }

    @Override
    public List<SchoolClassResponse> getDeletedClasses() {
        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {

            return schoolClassRepository.findAllByIsDeletedTrue()
                    .stream()
                    .map(SchoolClassMapper::toResponse)
                    .toList();
        }

        return schoolClassRepository
                .findAllByCreatedByAndIsDeletedTrue(currentUser)
                .stream()
                .map(SchoolClassMapper::toResponse)
                .toList();
    }

    @Override
    public List<SchoolClassResponse> getMyClasses() {

        User currentUser = securityService.getCurrentUser();

        return schoolClassRepository
                .findAllByTeacherAndIsDeletedFalse(currentUser)
                .stream()
                .map(SchoolClassMapper::toResponse)
                .toList();
    }

    @Override
    public SchoolClassResponse updateClass(Long id, UpdateSchoolClassRequest req) {

        SchoolClass schoolClass = findClassById(id);

        ownershipService.validateClassOwnership(schoolClass);

        User teacher = findTeacherById(req.getTeacherId());

        validateTeacherRole(teacher);

        ownershipService.validateUserOwnership(teacher);

        List<Student> students = findStudentsByIds(req.getStudentIds());

        ownershipService.validateStudentsOwnership(students);

        schoolClass.setName(req.getName());
        schoolClass.setTeacher(teacher);
        schoolClass.setStudents(students);

        SchoolClass updatedClass = schoolClassRepository.save(schoolClass);

        log.info(
                "Clase actualizada correctamente con id: {}",
                updatedClass.getId()
        );

        return SchoolClassMapper.toResponse(updatedClass);
    }

    @Override
    public void deleteClass(Long id) {

        SchoolClass schoolClass = findClassById(id);

        ownershipService.validateClassOwnership(schoolClass);

        schoolClass.setIsDeleted(true);

        schoolClassRepository.save(schoolClass);

        log.info(
                "Clase eliminada correctamente con id: {}",
                schoolClass.getId()
        );
    }

    @Override
    public void restoreClass(Long id) {

        SchoolClass schoolClass = schoolClassRepository.findByIdAndIsDeletedTrue(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorMessages.CLASS_NOT_FOUND
                        )
                );

        ownershipService.validateClassOwnership(schoolClass);

        if (!schoolClass.getIsDeleted()) {
            throw new ConflictException(
                    ErrorMessages.CLASS_ALREADY_ACTIVE
            );
        }

        schoolClass.setIsDeleted(false);

        schoolClassRepository.save(schoolClass);

        log.info(
                "Clase restaurada correctamente con id: {}",
                schoolClass.getId()
        );
    }

    private SchoolClass findClassById(Long id) {

        return schoolClassRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorMessages.CLASS_NOT_FOUND
                        )
                );
    }

    private User findTeacherById(Long teacherId) {

        return userRepository
                .findByIdAndIsDeletedFalse(teacherId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorMessages.USER_NOT_FOUND
                        )
                );
    }

    private List<Student> findStudentsByIds(List<Long> ids) {

        User currentUser = securityService.getCurrentUser();

        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        return studentRepository.findByIdInAndCreatedByAndIsDeletedFalse(ids, currentUser);
    }

    private void validateTeacherRole(User teacher) {

        if (teacher.getRole() != Role.PROFESOR) {
            throw new ConflictException(
                    ErrorMessages.USER_ROLE_NOT_PROFESOR
            );
        }
    }
}
