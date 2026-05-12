package com.utp.backend_incidencias.user.controller;

import com.utp.backend_incidencias.common.constants.SuccessMessages;
import com.utp.backend_incidencias.common.response.ApiResponse;
import com.utp.backend_incidencias.student.dto.response.StudentResponse;
import com.utp.backend_incidencias.user.dto.request.ChangePasswordRequest;
import com.utp.backend_incidencias.user.dto.request.CreateUserRequest;
import com.utp.backend_incidencias.user.dto.request.UpdateUserRequest;
import com.utp.backend_incidencias.user.dto.response.UserResponse;
import com.utp.backend_incidencias.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest req
    ) {

        UserResponse res = userService.createUser(req);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.<UserResponse>builder()
                                .success(true)
                                .message(SuccessMessages.USER_CREATED)
                                .data(res)
                                .build()
                );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> res = userService.getUsersByCurrentUser();

        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.USERS_RETRIEVED)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable Long id
    ) {

        UserResponse user = userService.getUserById(id);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message(SuccessMessages.USER_RETRIEVED)
                        .data(user)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @GetMapping("/deleted")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getDeletedUsers(){

        List<UserResponse> res = userService.getDeletedUsers();

        return ResponseEntity.ok(
                ApiResponse.<List<UserResponse>>builder()
                        .success(true)
                        .message(SuccessMessages.DELETED_USERS_FOUND)
                        .data(res)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest req
    ) {

        UserResponse updatedUser =
                userService.updateUser(id, req);

        return ResponseEntity.ok(
                ApiResponse.<UserResponse>builder()
                        .success(true)
                        .message(SuccessMessages.USER_UPDATED)
                        .data(updatedUser)
                        .build()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable Long id
    ) {

        userService.deleteUser(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(SuccessMessages.USER_DELETED)
                        .build()
        );
    }

    @PreAuthorize(
            "hasRole('ADMIN') or hasRole('COORDINADOR')"
    )
    @PatchMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreUser(
            @PathVariable Long id
    ) {

        userService.restoreUser(id);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(SuccessMessages.USERS_RETRIEVED)
                        .build()
        );
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/me/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        userService.changePassword(
                request
        );

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .success(true)
                        .message(SuccessMessages.PASSWORD_UPDATED)
                        .data(null)
                        .build()
        );
    }
}
