package com.utp.backend_incidencias.user.service.impl;

import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ConflictException;
import com.utp.backend_incidencias.common.exception.ForbiddenException;
import com.utp.backend_incidencias.common.exception.ResourceNotFoundException;
import com.utp.backend_incidencias.common.exception.UnauthorizedException;
import com.utp.backend_incidencias.user.dto.request.ChangePasswordRequest;
import com.utp.backend_incidencias.user.dto.request.CreateUserRequest;
import com.utp.backend_incidencias.user.dto.request.UpdateUserRequest;
import com.utp.backend_incidencias.user.dto.response.UserResponse;
import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.enums.Role;
import com.utp.backend_incidencias.user.mapper.UserMapper;
import com.utp.backend_incidencias.user.repository.UserRepository;
import com.utp.backend_incidencias.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest req) {
        validateUserData(req);

        User currentUser = getCurrentUser();
        User user = UserMapper.toEntity(req);

        user.setUsername(
                generateUsername(
                        req.getRole(),
                        req.getDni()
                )
        );

        user.setPassword(passwordEncoder.encode(req.getPassword()));

        user.setCreatedBy(currentUser);

        User savedUser = userRepository.save(user);

        log.info(
                "Usuario creado correctamente con username: {}",
                savedUser.getUsername()
        );

        return UserMapper.toResponse(savedUser);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findByIsDeletedFalse()
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getUsersByCurrentUser() {

        User currentUser = getCurrentUser();

        if (currentUser.getRole().equals(Role.ADMIN)) {
            return userRepository.findByIsDeletedFalse()
                    .stream()
                    .filter(u -> !Objects.equals(u.getUsername(), currentUser.getUsername()))
                    .map(UserMapper::toResponse)
                    .toList();
        }

        return userRepository.findByCreatedBy(currentUser)
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {

        User currentUser = getCurrentUser();
        User user = findUserById(id);

        if (currentUser.getRole() != Role.ADMIN &&
                !user.getCreatedBy().getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UpdateUserRequest req) {

        User currentUser = getCurrentUser();
        User user = findUserById(id);

        if (currentUser.getRole() != Role.ADMIN &&
                !user.getCreatedBy().getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }

        UserMapper.updateEntity(user, req);

        User updatedUser = userRepository.save(user);

        log.info(
                "Usuario actualizado correctamente con id: {}",
                updatedUser.getId()
        );

        return UserMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {

        User currentUser = getCurrentUser();
        User user = findUserById(id);

        if (currentUser.getRole() != Role.ADMIN &&
                !user.getCreatedBy().getId()
                        .equals(currentUser.getId())) {

            throw new ForbiddenException(
                    ErrorMessages.FORBIDDEN_ACCESS
            );
        }

        user.setIsDeleted(true);

        userRepository.save(user);

        log.info(
                "Usuario eliminado de forma segura con id: {}",
                user.getId()
        );
    }

    @Override
    public void changePassword(ChangePasswordRequest req) {

        User user = getCurrentUser();

        validatePasswordChange(req, user);

        user.setPassword(
                passwordEncoder.encode(req.getNewPassword())
        );

        userRepository.save(user);

        log.info(
                "Contraseña cambiada correctamente para el usuario con id: {}",
                user.getId()
        );
    }

    private void validateUserData(CreateUserRequest req) {

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException(ErrorMessages.EMAIL_ALREADY_EXISTS);
        }

        if (userRepository.existsByDni(req.getDni())) {
            throw new ConflictException(ErrorMessages.DNI_ALREADY_EXISTS);
        }
    }

    private User findUserById(Long id) {
        return userRepository
                .findByIdAndIsDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorMessages.USER_NOT_FOUND
                        )
                );
    }

    private String generateUsername(Role role, String dni) {
        String prefix = switch (role) {
            case ADMIN -> "A";
            case COORDINADOR -> "C";
            case PROFESOR -> "D";
            case PADRE -> "P";

            default -> throw new IllegalArgumentException(ErrorMessages.INVALID_ROLE);
        };
        return prefix + dni;
    }

    private User getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new UnauthorizedException(ErrorMessages.UNAUTHORIZED_ACCESS);
        }

        String username = auth.getName();
        log.info("Getting users by username: {}", username);

        return userRepository
                .findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorMessages.USER_NOT_FOUND
                        )
                );
    }

    private void validatePasswordChange(ChangePasswordRequest req, User user) {

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new ConflictException(ErrorMessages.CURRENT_PASSWORD_INCORRECT);
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new ConflictException(ErrorMessages.PASSWORDS_DO_NOT_MATCH);
        }
    }
}
