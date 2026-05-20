package com.utp.backend_incidencias.user.repository;

import com.utp.backend_incidencias.user.entity.User;
import com.utp.backend_incidencias.user.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Optional<User> findByDniAndRoleAndIsDeletedFalse(String dni, Role role);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    boolean existsByUsername(String username);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByDniAndIdNot(String dni, Long id);

    List<User> findAllByRoleAndIsDeletedFalse(Role role);

    List<User> findAllByRoleAndIsDeletedTrue(Role role);

    List<User> findAllByRoleAndCreatedByAndIsDeletedFalse(Role role, User createdBy);

    List<User> findAllByRoleAndCreatedByAndIsDeletedTrue(Role role, User createdBy);

    List<User> findAllByIsDeletedFalse();

    List<User> findAllByIsDeletedTrue();

    List<User> findAllByCreatedByAndIsDeletedFalse(User createdBy);

    List<User> findAllByCreatedByAndIsDeletedTrue(User createdBy);
}
