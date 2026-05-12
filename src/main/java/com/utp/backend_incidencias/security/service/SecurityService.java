package com.utp.backend_incidencias.security.service;

import com.utp.backend_incidencias.common.constants.ErrorMessages;
import com.utp.backend_incidencias.common.exception.ResourceNotFoundException;
import com.utp.backend_incidencias.common.exception.UnauthorizedException;
import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SecurityService {

    private final UserRepository userRepository;

    public User getCurrentUser() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null ||
                !auth.isAuthenticated() ||
                !(auth.getPrincipal() instanceof UserDetails)) {
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
}
