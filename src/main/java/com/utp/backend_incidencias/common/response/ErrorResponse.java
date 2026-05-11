package com.utp.backend_incidencias.common.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    private String message;

    private String error;

    private Integer status;

    private String path;

    private LocalDateTime timestamp;
}