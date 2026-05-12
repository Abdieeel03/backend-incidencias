package com.utp.backend_incidencias.security.service;

import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ForbiddenException;
import com.utp.backend_incidencias.common.exception.UnauthorizedException;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.student.entity.Student;
import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OwnershipService {

    private final SecurityService securityService;

    private User current() {
        return securityService.getCurrentUser();
    }

    public void validateUserOwnership(User targetUser) {

        User currentUser = current();

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (targetUser.getCreatedBy() == null ||
                !targetUser.getCreatedBy()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }
    }

    public void validateStudentOwnership(Student targetStudent) {
        User currentUser = current();

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (targetStudent.getCreatedBy() == null ||
                !targetStudent.getCreatedBy()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.UNAUTHORIZED_ACCESS
            );
        }
    }

    public void validateStudentsOwnership(List<Student> students) {

        students.forEach(this::validateStudentOwnership);
    }

    public void validateClassOwnership(SchoolClass schoolClass) {

        User currentUser = current();

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (schoolClass.getCreatedBy() == null ||
                !schoolClass.getCreatedBy()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }
    }

    public void validateClassAccess(SchoolClass schoolClass) {

        User currentUser = current();

        if (currentUser.getRole() == Role.ADMIN) return;

        if (currentUser.getRole() == Role.COORDINADOR) {
            validateClassOwnership(schoolClass);
            return;
        }

        if (currentUser.getRole() == Role.PROFESOR) {
            validateTeacherAssignment(schoolClass);
            return;
        }

        throw new ForbiddenException(
                ErrorMessages.FORBIDDEN_ACCESS
        );
    }

    public void validateTeacherAssignment(SchoolClass schoolClass) {

        User currentUser = current();

        if (schoolClass.getTeacher() == null ||
                !schoolClass.getTeacher()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }
    }
}
