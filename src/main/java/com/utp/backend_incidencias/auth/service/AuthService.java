package com.utp.backend_incidencias.auth.service;

import com.utp.backend_incidencias.auth.dto.request.*;
import com.utp.backend_incidencias.auth.dto.response.AuthResponse;
import com.utp.backend_incidencias.auth.factory.UserFactory;
import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ConflictException;
import com.utp.backend_incidencias.common.exception.ResourceNotFoundException;
import com.utp.backend_incidencias.common.exception.UnauthorizedException;
import com.utp.backend_incidencias.security.jwt.JwtService;
import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByDni(req.getDni())) {
            throw new ConflictException(ErrorMessages.DNI_ALREADY_EXISTS);
        }

        User user = UserFactory.createCoordinator(req, passwordEncoder);

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new ConflictException(
                    ErrorMessages.USERNAME_ALREADY_EXISTS
            );
        }

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        log.info("Usuario registrado: {}", savedUser.getUsername());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    public AuthResponse login(LoginRequest req) {

        User user = userRepository.findByUsernameAndIsDeletedFalse(req.getUsername())
                .orElseThrow(() -> new UnauthorizedException(ErrorMessages.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new UnauthorizedException(ErrorMessages.INVALID_CREDENTIALS);
        }

        String token = jwtService.generateToken(user);

        log.info("Login exitoso: {}", user.getUsername());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationTime())
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }

    public void forgotPassword(ForgotPasswordRequest req) {

        User user = userRepository.findByEmailAndIsDeletedFalse(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        log.info(
                "Solicitud de recuperación para usuario con id: {}",
                user.getId()
        );

    }

    public void resetPassword(ResetPasswordRequest req){

        User user = userRepository.findByEmailAndIsDeletedFalse(req.getEmail())
                .orElseThrow(()-> new ResourceNotFoundException(ErrorMessages.USER_NOT_FOUND));

        if(!req.getNewPassword().equals(req.getConfirmPassword())){
            throw new ConflictException(ErrorMessages.PASSWORDS_DO_NOT_MATCH);
        }

        user.setPassword(
                passwordEncoder.encode(req.getNewPassword())
        );

        userRepository.save(user);

        log.info(
                "Contraseña cambiada correctamente para el usuario con id: {}",
                user.getId()
        );
        
    }

    public void verifyResetCode(VerifyResetCodeRequest req) {

        User user = userRepository
                .findByEmailAndIsDeletedFalse(req.getEmail())
                .orElseThrow(() ->
                        new ConflictException(
                                ErrorMessages.INVALID_CREDENTIALS
                        )
                );


        //Validación temporal solo con dni
        if(!user.getDni().equals(req.getCode())){
            throw new ConflictException(
                    ErrorMessages.INVALID_CREDENTIALS
            );
        }

        log.info(
                "Código validado para usuario con id: {}",
                user.getId()
        );
    }

}
