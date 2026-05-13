package com.utp.backend_incidencias.auth.factory;

import com.utp.backend_incidencias.auth.dto.request.RegisterRequest;
import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserFactory {

    private UserFactory() {}

    public static User createCoordinator(RegisterRequest req, PasswordEncoder passwordEncoder) {

        return User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .dni(req.getDni())
                .username(generateUsername(req.getDni()))
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.COORDINADOR)
                .build();
    }

    private static String generateUsername(String dni){
        return "C" + dni;
    }
}
