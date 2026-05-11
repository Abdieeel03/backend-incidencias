package com.utp.backend_incidencias.user.dto.response;

import com.utp.backend_incidencias.user.enums.Role;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private String name;

    private String dni;

    private Role role;

    private Long createdById;

    private String createdByUsername;
}
