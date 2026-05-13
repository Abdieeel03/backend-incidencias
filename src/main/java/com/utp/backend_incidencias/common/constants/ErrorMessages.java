package com.utp.backend_incidencias.common.constants;

public class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String USER_NOT_FOUND =
            "Usuario no encontrado";

    public static final String USER_ALREADY_ACTIVE =
            "El usuario ya se encuentra activo";

    public static final String STUDENT_NOT_FOUND =
            "Estudiante no encontrado";

    public static final String STUDENT_ALREADY_ACTIVE =
            "El estudiante ya se encuentra activo";

    public static final String CLASS_NOT_FOUND =
            "Clase no encontrada";

    public static final String CLASS_ALREADY_ACTIVE =
            "La clase ya se encuentra activo";

    public static final String INCIDENT_NOT_FOUND =
            "Incidente no encontrado";

    public static final String EMAIL_ALREADY_EXISTS =
            "El email ya está registrado";

    public static final String DNI_ALREADY_EXISTS =
            "El DNI ya está registrado";

    public static final String USERNAME_ALREADY_EXISTS =
            "El username ya está registrado";

    public static final String INVALID_CREDENTIALS =
            "Credenciales inválidas";

    public static final String PASSWORDS_DO_NOT_MATCH =
            "Las contraseñas no coinciden";

    public static final String CURRENT_PASSWORD_INCORRECT =
            "La contraseña actual es incorrecta";

    public static final String FORBIDDEN_ACCESS =
            "Acceso denegado: no tienes permisos para realizar esta acción";

    public static final String UNAUTHORIZED_ACCESS =
            "Acceso no autorizado. Por favor inicia sesión nuevamente";

    public static final String ADMIN_ASSIGNMENT_NOT_ALLOWED =
            "Asignación no permitida. No puedes asignar rol: ADMIN";

    public static final String USER_ROLE_NOT_PADRE =
            "El usuario seleccionado no es un padre";

    public static final String USER_ROLE_NOT_PROFESOR =
            "El usuario seleccionado no es un profesor";

    public static final String STUDENT_NOT_IN_CLASS =
            "El estudiante no esta en esta clase";
}