package com.utp.backend_incidencias.security.service;

import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ForbiddenException;
import com.utp.backend_incidencias.common.exception.UnauthorizedException;
import com.utp.backend_incidencias.student.entity.Student;
import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OwnershipService {

    private final SecurityService securityService;

    public void validateUserOwnership(User targetUser) {

        User currentUser = securityService.getCurrentUser();

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
        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }

        if (targetStudent.getCreatedBy() == null ||
                !targetStudent.getCreatedBy()
                        .getId()
                        .equals(currentUser.getId())) {

            throw new UnauthorizedException(
                    ErrorMessages.UNAUTHORIZED_ACCESS
            );
        }
    }
}
