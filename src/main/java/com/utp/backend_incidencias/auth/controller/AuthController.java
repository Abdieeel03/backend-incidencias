package com.utp.backend_incidencias.auth.controller;

import com.utp.backend_incidencias.auth.dto.request.LoginRequest;
import com.utp.backend_incidencias.auth.dto.request.RegisterRequest;
import com.utp.backend_incidencias.auth.dto.response.AuthResponse;
import com.utp.backend_incidencias.auth.service.AuthService;
import com.utp.backend_incidencias.common.constants.SuccessMessages;
import com.utp.backend_incidencias.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest req
            ) {

        AuthResponse res = authService.register(req);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message(SuccessMessages.USER_REGISTERED)
                        .data(res)
                        .build()
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest req
    ) {

        AuthResponse res = authService.login(req);

        return ResponseEntity.ok(
                ApiResponse.<AuthResponse>builder()
                        .success(true)
                        .message(SuccessMessages.USER_LOGUED)
                        .data(res)
                        .build()
        );
    }
}
