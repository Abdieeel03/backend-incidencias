package com.utp.backend_incidencias.user.service.impl;

import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ConflictException;
import com.utp.backend_incidencias.common.exception.ForbiddenException;
import com.utp.backend_incidencias.common.exception.ResourceNotFoundException;
import com.utp.backend_incidencias.common.helper.CodeGenerationHelper;
import com.utp.backend_incidencias.security.service.OwnershipService;
import com.utp.backend_incidencias.security.service.SecurityService;
import com.utp.backend_incidencias.user.dto.request.ChangePasswordRequest;
import com.utp.backend_incidencias.user.dto.request.CoordinatorUpdateUserRequest;
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
    private final SecurityService securityService;
    private final OwnershipService ownershipService;
    private final CodeGenerationHelper codeGenerationHelper;

    @Override
    public UserResponse createUser(CreateUserRequest req) {
        validateUserData(req);

        User currentUser = securityService.getCurrentUser();
        User user = UserMapper.toEntity(req);

        user.setUsername(
                codeGenerationHelper.generateUsername(
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
        return getUsersByCurrentUser();
    }

    @Override
    public List<UserResponse> getUsersByCurrentUser() {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole().equals(Role.ADMIN)) {
            return userRepository.findAllByIsDeletedFalse()
                    .stream()
                    .filter(u -> !Objects.equals(u.getUsername(), currentUser.getUsername()))
                    .map(UserMapper::toResponse)
                    .toList();
        }

        return userRepository
                .findAllByCreatedByAndIsDeletedFalse(currentUser)
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getUsersByRole(Role role) {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {

            return userRepository
                    .findAllByRoleAndIsDeletedFalse(role)
                    .stream()
                    .map(UserMapper::toResponse)
                    .toList();

        }

        return userRepository
                .findAllByRoleAndCreatedByAndIsDeletedFalse(role, currentUser)
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getParentByDni(String dni) {
        User parent = userRepository
                .findByDniAndRoleAndIsDeletedFalse(dni, Role.PADRE)
                .orElseThrow(() -> new ResourceNotFoundException(
                        ErrorMessages.USER_NOT_FOUND
                ));

        ownershipService.validateUserOwnership(parent);

        return UserMapper.toResponse(parent);
    }

    @Override
    public List<UserResponse> getDeletedUsersByRole(Role role) {

        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {

            return userRepository
                    .findAllByRoleAndIsDeletedTrue(role)
                    .stream()
                    .map(UserMapper::toResponse)
                    .toList();

        }

        return userRepository
                .findAllByRoleAndCreatedByAndIsDeletedTrue(role, currentUser)
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public List<UserResponse> getDeletedUsers() {
        User currentUser = securityService.getCurrentUser();

        if (currentUser.getRole() == Role.ADMIN) {

            return userRepository.findAllByIsDeletedTrue()
                    .stream()
                    .filter(user ->
                            !user.getId().equals(currentUser.getId())
                    )
                    .map(UserMapper::toResponse)
                    .toList();
        }

        return userRepository
                .findAllByCreatedByAndIsDeletedTrue(currentUser)
                .stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = findUserById(id);

        ownershipService.validateUserOwnership(user);

        return UserMapper.toResponse(user);
    }

    @Override
    public UserResponse updateUser(UpdateUserRequest req) {

        User user = securityService.getCurrentUser();

        if (userRepository.existsByEmailAndIdNot(req.getEmail(), user.getId())) {
            throw new ConflictException(ErrorMessages.EMAIL_ALREADY_EXISTS);
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
    public UserResponse updateUserByCoordinator(Long id, CoordinatorUpdateUserRequest req) {

        User user = findUserById(id);

        ownershipService.validateUserOwnership(user);

        if (userRepository.existsByDniAndIdNot(req.getDni(), id)) {
            throw new ConflictException(ErrorMessages.DNI_ALREADY_EXISTS);
        }

        user.setDni(req.getDni());

        if (req.getRole() == Role.ADMIN) {
            throw new ForbiddenException(
                    ErrorMessages.ADMIN_ASSIGNMENT_NOT_ALLOWED
            );
        }

        user.setRole(req.getRole());

        user.setUsername(
                codeGenerationHelper.generateUsername(
                        req.getRole(),
                        req.getDni()
                )
        );

        User updatedUser = userRepository.save(user);

        log.info(
                "Usuario actualizado por coordinador correctamente con id: {}",
                updatedUser.getId()
        );

        return UserMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {

        User user = findUserById(id);

        ownershipService.validateUserOwnership(user);

        user.setIsDeleted(true);

        userRepository.save(user);

        log.info(
                "Usuario eliminado de forma segura con id: {}",
                user.getId()
        );
    }

    @Override
    public void changePassword(ChangePasswordRequest req) {

        User user = securityService.getCurrentUser();

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

    @Override
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ErrorMessages.USER_NOT_FOUND
                        )
                );

        ownershipService.validateUserOwnership(user);

        if (!user.getIsDeleted()) {
            throw new ConflictException(
                    ErrorMessages.USER_ALREADY_ACTIVE
            );
        }

        user.setIsDeleted(false);

        userRepository.save(user);

        log.info(
                "Usuario restaurado correctamente con id: {}",
                user.getId()
        );
    }

    @Override
    public UserResponse getMeUser() {
        return null;
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

    private void validatePasswordChange(ChangePasswordRequest req, User user) {

        if (!passwordEncoder.matches(req.getCurrentPassword(), user.getPassword())) {
            throw new ConflictException(ErrorMessages.CURRENT_PASSWORD_INCORRECT);
        }

        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new ConflictException(ErrorMessages.PASSWORDS_DO_NOT_MATCH);
        }
    }
}
