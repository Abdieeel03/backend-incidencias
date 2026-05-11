package com.utp.backend_incidencias.user.entity;

import com.utp.backend_incidencias.common.entity.BaseEntity;
import com.utp.backend_incidencias.incident.entity.Incident;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.student.entity.Student;
import com.utp.backend_incidencias.user.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String username;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @OneToMany(mappedBy = "parent")
    private List<Student> students;

    @OneToMany(mappedBy = "teacher")
    private List<SchoolClass> classes;

    @OneToMany(mappedBy = "teacher")
    private List<Incident> incidents;

}
