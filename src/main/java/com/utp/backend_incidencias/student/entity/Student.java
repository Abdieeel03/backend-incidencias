package com.utp.backend_incidencias.student.entity;

import com.utp.backend_incidencias.common.entity.BaseEntity;
import com.utp.backend_incidencias.incident.entity.Incident;
import com.utp.backend_incidencias.schoolclass.entity.SchoolClass;
import com.utp.backend_incidencias.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "students")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(nullable = false, unique = true, length = 8)
    private String dni;

    @Column(name = "student_code", nullable = false, unique = true, length = 10, updatable = false)
    private String studentCode;

    @Column(nullable = false)
    @Builder.Default
    protected Boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private User parent;

    @ManyToMany(mappedBy = "students")
    private List<SchoolClass> classes;

    @OneToMany(mappedBy = "student")
    private List<Incident> incidents;
}
