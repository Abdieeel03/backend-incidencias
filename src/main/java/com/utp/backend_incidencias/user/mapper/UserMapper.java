package com.utp.backend_incidencias.user.mapper;

import com.utp.backend_incidencias.user.dto.request.ChangePasswordRequest;
import com.utp.backend_incidencias.user.dto.request.CreateUserRequest;
import com.utp.backend_incidencias.user.dto.request.UpdateUserRequest;
import com.utp.backend_incidencias.user.dto.response.UserResponse;
import com.utp.backend_incidencias.user.entity.User;

public class UserMapper {

    private UserMapper() {}

    public static User toEntity(CreateUserRequest req){
        return User.builder()
                .email(req.getEmail())
                .name(req.getName())
                .dni(req.getDni())
                .password(req.getPassword())
                .role(req.getRole())
                .build();
    }

    public static UserResponse toResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .name(user.getName())
                .dni(user.getDni())
                .role(user.getRole())
                .build();
    }

    public static void updateEntity(User user, UpdateUserRequest req){
        user.setEmail(req.getEmail());
        user.setName(req.getName());
        user.setDni(req.getDni());
        user.setRole(req.getRole());
    }

    public static void updatePassword(User user, ChangePasswordRequest req){
        user.setPassword(req.getNewPassword());
    }
}
