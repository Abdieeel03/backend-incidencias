package com.utp.backend_incidencias.user.service;

import com.utp.backend_incidencias.user.dto.request.ChangePasswordRequest;
import com.utp.backend_incidencias.user.dto.request.CoordinatorUpdateUserRequest;
import com.utp.backend_incidencias.user.dto.request.CreateUserRequest;
import com.utp.backend_incidencias.user.dto.request.UpdateUserRequest;
import com.utp.backend_incidencias.user.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest req);

    List<UserResponse> getAllUsers();

    List<UserResponse> getUsersByCurrentUser();

    UserResponse getUserById(Long id);

    UserResponse updateUser(Long id, UpdateUserRequest req);

    UserResponse updateUserByCoordinator(Long id, CoordinatorUpdateUserRequest req);

    void deleteUser(Long id);

    void changePassword(
            ChangePasswordRequest req
    );
}
