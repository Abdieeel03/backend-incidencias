package com.utp.backend_incidencias.user.repository;

import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsernameOrEmail(String username, String email);

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    boolean existsByUsername(String username);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByDniAndIdNot(String dni, Long id);

    List<User> findByRole(Role role);

    List<User> findAllByIsDeletedFalse();

    List<User> findAllByCreatedByAndIsDeletedFalse(User createdBy);
}
