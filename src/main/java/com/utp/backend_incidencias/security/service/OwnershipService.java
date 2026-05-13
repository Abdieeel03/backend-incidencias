package com.utp.backend_incidencias.security.service;

import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ForbiddenException;
import com.utp.backend_incidencias.incident.entity.Incident;
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

        if (targetUser.getId().equals(currentUser.getId())) {
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

    private void validateTeacherStudentRelation(Student student) {

        User currentUser = securityService.getCurrentUser();

        boolean teachesStudent = student.getClasses()
                .stream()
                .anyMatch(schoolClass ->
                        schoolClass.getTeacher() != null &&
                                schoolClass.getTeacher()
                                        .getId()
                                        .equals(currentUser.getId())
                );

        if (!teachesStudent) {

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

    private void validateParentStudentRelation(Student student) {

        User currentUser = securityService.getCurrentUser();

        if (student.getParent() == null ||
                !student.getParent()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }
    }

    public void validateStudentAccess(Student student) {

        User currentUser = current();

        if (currentUser.getRole() == Role.ADMIN) return;

        if (currentUser.getRole() == Role.COORDINADOR) {
            validateStudentOwnership(student);
            return;
        }

        if (currentUser.getRole() == Role.PROFESOR) {
            validateTeacherStudentRelation(student);
            return;
        }

        if (currentUser.getRole() == Role.PADRE) {
            validateParentStudentRelation(student);
            return;
        }

        throw new ForbiddenException(
                ErrorMessages.FORBIDDEN_ACCESS
        );
    }

    public void validateIncidentAccess(Incident incident) {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (currentUser.getRole() == Role.COORDINADOR) {

            validateCoordinatorIncidentAccess(incident);
            return;
        }

        if (currentUser.getRole() == Role.PROFESOR) {

            validateTeacherIncidentAccess(incident);
            return;
        }

        if (currentUser.getRole() == Role.PADRE) {

            validateParentIncidentAccess(incident);
            return;
        }

        throw new ForbiddenException(
                ErrorMessages.FORBIDDEN_ACCESS
        );
    }

    public void validateCoordinatorIncidentAccess(Incident incident) {

        User currentUser = securityService.getCurrentUser();

        User teacher = incident.getTeacher();

        if (teacher == null ||
                teacher.getCreatedBy() == null ||
                !teacher.getCreatedBy()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }
    }

    public void validateTeacherIncidentAccess(Incident incident) {

        User currentUser = securityService.getCurrentUser();

        if (incident.getTeacher() == null ||
                !incident.getTeacher()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }
    }

    public void validateParentIncidentAccess(Incident incident) {

        User currentUser = securityService.getCurrentUser();

        if (incident.getStudent() == null ||
                incident.getStudent().getParent() == null ||
                !incident.getStudent()
                        .getParent()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }
    }


}
