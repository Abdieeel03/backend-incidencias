package com.utp.backend_incidencias.user.mapper;

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
                .imageUrl(req.getImageUrl())
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
                .imageUrl(user.getImageUrl())
                .role(user.getRole())
                .createdById(
                        user.getCreatedBy() != null
                                ? user.getCreatedBy().getId()
                                : null
                )
                .createdByUsername(
                        user.getCreatedBy() != null
                                ? user.getCreatedBy().getUsername()
                                : null
                )
                .build();
    }

    public static void updateEntity(User user, UpdateUserRequest req){
        user.setEmail(req.getEmail());
        user.setImageUrl(req.getImageUrl());
    }

}
