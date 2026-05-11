package com.utp.backend_incidencias.common.constants;

public class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String USER_NOT_FOUND =
            "Usuario no encontrado";

    public static final String STUDENT_NOT_FOUND =
            "Estudiante no encontrado";

    public static final String CLASS_NOT_FOUND =
            "Clase no encontrada";

    public static final String INCIDENT_NOT_FOUND =
            "Incidente no encontrado";

    public static final String EMAIL_ALREADY_EXISTS =
            "El email ya está registrado";

    public static final String DNI_ALREADY_EXISTS =
            "El DNI ya está registrado";

    public static final String INVALID_CREDENTIALS =
            "Credenciales inválidas";

    public static final String PASSWORDS_DO_NOT_MATCH =
            "Las contraseñas no coinciden";

    public static final String CURRENT_PASSWORD_INCORRECT =
            "La contraseña actual es incorrecta";

    public static final String INVALID_ROLE =
            "Rol inválido para generación de username";

    public static final String FORBIDDEN_ACCESS =
            "Acceso denegado: no tienes permisos para realizar esta acción";

    public static final String UNAUTHORIZED_ACCESS =
            "Acceso no autorizado. Por favor inicia sesión nuevamente";

    public static final String ADMIN_ASSIGNMENT_NOT_ALLOWED =
            "Asignación no permitida. No puedes asignar rol: ADMIN";
}