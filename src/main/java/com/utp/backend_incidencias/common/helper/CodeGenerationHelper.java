package com.utp.backend_incidencias.common.helper;

import com.utp.backend_incidencias.user.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerationHelper {

    public String generateStudentCode(String dni) {
        return "E" + dni;
    }

    public String generateUsername(Role role, String dni) {

        String prefix = switch (role) {
            case ADMIN -> "A";
            case COORDINADOR -> "C";
            case PROFESOR -> "D";
            case PADRE -> "P";
        };

        return prefix + dni;
    }
}
