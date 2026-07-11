package com.utp.backend_incidencias.student.service.impl;

import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ConflictException;
import com.utp.backend_incidencias.common.exception.ResourceNotFoundException;
import com.utp.backend_incidencias.common.helper.CodeGenerationHelper;
import com.utp.backend_incidencias.security.service.OwnershipService;
import com.utp.backend_incidencias.security.service.SecurityService;
import com.utp.backend_incidencias.student.dto.request.CreateStudentRequest;
import com.utp.backend_incidencias.student.dto.request.UpdateStudentRequest;
import com.utp.backend_incidencias.student.dto.response.StudentDetailResponse;
import com.utp.backend_incidencias.student.dto.response.StudentResponse;
import com.utp.backend_incidencias.student.entity.Student;
import com.utp.backend_incidencias.student.mapper.StudentMapper;
import com.utp.backend_incidencias.student.repository.StudentRepository;
import com.utp.backend_incidencias.student.service.StudentService;
import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.enums.Role;
import com.utp.backend_incidencias.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

        private final StudentRepository studentRepository;
        private final UserRepository userRepository;
        private final SecurityService securityService;
        private final OwnershipService ownershipService;
        private final CodeGenerationHelper codeGenerationHelper;

        @Override
        public StudentResponse createStudent(CreateStudentRequest req) {

                User currentUser = securityService.getCurrentUser();

                verifyExistingDni(req.getDni());

                User parent = userRepository
                                .findByIdAndIsDeletedFalse(req.getParentId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                ErrorMessages.USER_NOT_FOUND));

                ownershipService.validateUserOwnership(parent);

                verifyRolePadre(parent.getRole());

                Student student = StudentMapper.toEntity(req);

                student.setStudentCode(
                                codeGenerationHelper.generateStudentCode(
                                                req.getDni()));

                student.setParent(parent);

                student.setCreatedBy(currentUser);

                Student savedStudent = studentRepository.save(student);

                log.info(
                                "Estudiante creado correctamente con id: {}",
                                savedStudent.getId());

                return StudentMapper.toResponse(savedStudent);
        }

        @Override
        public StudentResponse getStudentById(Long id) {

                Student student = findStudentById(id);

                ownershipService.validateStudentOwnership(student);

                return StudentMapper.toResponse(student);
        }

        @Override
        public List<StudentResponse> getAllStudents() {

                User currentUser = securityService.getCurrentUser();

                if (currentUser.getRole() == Role.ADMIN) {
                        return studentRepository
                                        .findAllByIsDeletedFalse()
                                        .stream()
                                        .map(StudentMapper::toResponse)
                                        .toList();
                }

                return studentRepository
                                .findAllByCreatedByAndIsDeletedFalse(currentUser)
                                .stream()
                                .map(StudentMapper::toResponse)
                                .toList();
        }

        @Override
        public StudentDetailResponse getStudentDetails(Long id) {

                Student student = studentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.STUDENT_NOT_FOUND));

                ownershipService.validateStudentAccess(student);

                return StudentMapper.toDetailResponse(student);
        }

        @Override
        public List<StudentResponse> getDeletedStudents() {

                User currentUser = securityService.getCurrentUser();

                if (currentUser.getRole() == Role.ADMIN) {

                        return studentRepository.findAllByIsDeletedTrue()
                                        .stream()
                                        .map(StudentMapper::toResponse)
                                        .toList();
                }

                return studentRepository
                                .findAllByCreatedByAndIsDeletedTrue(currentUser)
                                .stream()
                                .map(StudentMapper::toResponse)
                                .toList();
        }

        @Override
        public List<StudentResponse> getStudentsByParentId(Long parentId) {

                User currentUser = securityService.getCurrentUser();

                if (currentUser.getRole() == Role.PADRE) {

                        return studentRepository
                                        .findAllByParentAndIsDeletedFalse(currentUser)
                                        .stream()
                                        .map(StudentMapper::toResponse)
                                        .toList();
                }

                User parent = userRepository
                                .findByIdAndIsDeletedFalse(parentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                ErrorMessages.USER_NOT_FOUND));

                ownershipService.validateUserOwnership(parent);

                verifyRolePadre(parent.getRole());

                return studentRepository
                                .findAllByParentAndIsDeletedFalse(parent)
                                .stream()
                                .map(StudentMapper::toResponse)
                                .toList();
        }

        @Override
        public List<StudentResponse> searchStudents(String query) {
                User currentUser = securityService.getCurrentUser();

                return studentRepository.searchStudents(query)
                                .stream()
                                .filter(student -> student.getCreatedBy()
                                                .getId()
                                                .equals(currentUser.getId()))
                                .map(StudentMapper::toResponse)
                                .toList();
        }

        @Override
        public StudentResponse updateStudent(Long id, UpdateStudentRequest req) {

                Student student = findStudentById(id);

                ownershipService.validateStudentOwnership(student);

                if (studentRepository.existsByDniAndIdNot(
                                req.getDni(),
                                student.getId())) {
                        throw new ConflictException(
                                        ErrorMessages.DNI_ALREADY_EXISTS);
                }

                User parent = userRepository
                                .findByIdAndIsDeletedFalse(req.getParentId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                ErrorMessages.USER_NOT_FOUND));

                ownershipService.validateUserOwnership(parent);

                verifyRolePadre(parent.getRole());

                StudentMapper.updateEntity(student, req);

                student.setStudentCode(
                                codeGenerationHelper.generateStudentCode(
                                                student.getDni()));

                student.setParent(parent);

                Student updatedStudent = studentRepository.save(student);

                log.info(
                                "Estudiante actualizado correctamente con id: {}",
                                updatedStudent.getId());

                return StudentMapper.toResponse(updatedStudent);
        }

        @Override
        public void deleteStudent(Long id) {

                Student student = findStudentById(id);

                ownershipService.validateStudentOwnership(student);

                student.setIsDeleted(true);

                studentRepository.save(student);

                log.info(
                                "Estudiante eliminado correctamente con id: {}",
                                student.getId());
        }

        @Override
        public void restoreStudent(Long id) {

                Student student = studentRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                ErrorMessages.STUDENT_NOT_FOUND));

                ownershipService.validateStudentOwnership(student);

                if (!student.getIsDeleted()) {
                        throw new ConflictException(
                                        ErrorMessages.STUDENT_ALREADY_ACTIVE);
                }

                student.setIsDeleted(false);

                studentRepository.save(student);

                log.info(
                                "Estudiante restaurado correctamente con id: {}",
                                student.getId());
        }

        private Student findStudentById(Long id) {

                return studentRepository
                                .findByIdAndIsDeletedFalse(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                ErrorMessages.STUDENT_NOT_FOUND));
        }

        private void verifyExistingDni(String dni) {

                if (studentRepository.existsByDni(dni)) {
                        throw new ConflictException(
                                        ErrorMessages.DNI_ALREADY_EXISTS);
                }
        }

        private void verifyRolePadre(Role role) {

                if (role != Role.PADRE) {
                        throw new ConflictException(
                                        ErrorMessages.USER_ROLE_NOT_PADRE);
                }
        }
}
