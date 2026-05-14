# 🎓 Backend Incidencias API

API REST para la gestión de incidencias estudiantiles, desarrollada con **Spring Boot 3.5** y **Java 21**.

---

## 📋 Tabla de Contenidos

- [Requisitos Previos](#-requisitos-previos)
- [Inicio Rápido con Docker Compose (Recomendado)](#-inicio-rápido-con-docker-compose-recomendado)
- [Inicio Manual (Gradle + Docker)](#-inicio-manual-gradle--docker)
- [Verificar que Funciona](#-verificar-que-funciona)
- [Documentación de la API](#-documentación-de-la-api)
- [Estructura del Proyecto](#-estructura-del-proyecto)
- [Tecnologías Utilizadas](#-tecnologías-utilizadas)
- [Solución de Problemas](#-solución-de-problemas)

---

## 🔧 Requisitos Previos

| Herramienta | Docker Compose | Inicio Manual | Enlace de descarga |
|---|:---:|:---:|---|
| **Docker** | ✅ Requerido | ✅ Requerido | [Docker Desktop](https://www.docker.com/products/docker-desktop/) |
| **Java JDK 21** | ❌ No necesario | ✅ Requerido | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java21) / [Adoptium](https://adoptium.net/) |
| **Git** | ✅ Requerido | ✅ Requerido | [Git](https://git-scm.com/downloads) |

### Verificar instalaciones

```bash
docker --version  # Debe mostrar 20 o superior
git --version

# Solo si vas a usar el inicio manual:
java -version     # Debe mostrar 21 o superior
```

---

## 🐳 Inicio Rápido con Docker Compose (Recomendado)

> Esta opción levanta **PostgreSQL + la API** con un solo comando. No necesitas tener Java instalado.

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/backend-incidencias.git
cd backend-incidencias
```

### 2. (Opcional) Configurar variables de entorno

El Docker Compose ya incluye valores por defecto para desarrollo, así que puedes ejecutarlo directamente sin crear ningún archivo `.env`.

Si deseas personalizar las claves JWT u otras configuraciones, crea un archivo `.env`:

**Linux / macOS:**
```bash
cp .env.example .env
```

**Windows (CMD):**
```cmd
copy .env.example .env
```

Luego edítalo con tus valores. Las variables que configures en `.env` sobreescribirán los valores por defecto del Docker Compose.

> [!NOTE]
> Las variables de conexión a la base de datos (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) **no necesitan configurarse** en el `.env` para Docker Compose, ya que se configuran automáticamente dentro de la red de Docker.

### 3. Levantar todo

```bash
docker compose up -d
```

La primera vez tardará unos minutos mientras construye la imagen de la API. Las ejecuciones siguientes serán mucho más rápidas gracias al caché de Docker.

Esto levanta:

| Servicio | Contenedor | Puerto | Descripción |
|---|---|---|---|
| **PostgreSQL 17** | `postgres-incidencias` | `5432` | Base de datos con volumen persistente y health check |
| **API Spring Boot** | `api-incidencias` | `8080` | API REST (espera a que PostgreSQL esté listo) |

> [!TIP]
> Si tienes una versión antigua de Docker que no soporta `docker compose` (sin guión), usa `docker-compose up -d` en su lugar.

### Comandos útiles

```bash
# Ver el estado de los contenedores
docker compose ps

# Ver los logs en tiempo real
docker compose logs -f

# Ver solo los logs de la API
docker compose logs -f api

# Detener todos los servicios
docker compose down

# Detener y eliminar los datos de la base de datos
docker compose down -v

# Reconstruir la imagen después de cambios en el código
docker compose up -d --build

# Conectarse a la base de datos desde terminal
docker exec -it postgres-incidencias psql -U admin -d incidencias_db
```

---

## 🔨 Inicio Manual (Gradle + Docker)

> Esta opción levanta PostgreSQL con Docker y ejecuta la API directamente con Gradle. Útil para desarrollo activo con hot-reload.

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/backend-incidencias.git
cd backend-incidencias
```

### 2. Levantar PostgreSQL con Docker

```bash
docker run -d \
  --name postgres-incidencias \
  -e POSTGRES_DB=incidencias_db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin123 \
  -p 5432:5432 \
  postgres:17-alpine
```

<details>
<summary>📖 Explicación de los parámetros</summary>

| Parámetro | Descripción |
|---|---|
| `-d` | Ejecuta el contenedor en segundo plano |
| `--name postgres-incidencias` | Nombre del contenedor |
| `-e POSTGRES_DB=incidencias_db` | Nombre de la base de datos a crear |
| `-e POSTGRES_USER=admin` | Usuario de la base de datos |
| `-e POSTGRES_PASSWORD=admin123` | Contraseña del usuario |
| `-p 5432:5432` | Mapea el puerto 5432 del contenedor al puerto 5432 de tu máquina |
| `postgres:17-alpine` | Imagen de PostgreSQL 17 (versión ligera) |

</details>

<details>
<summary>🖥️ Alternativa: Usar Docker Desktop</summary>

1. Abre **Docker Desktop**.
2. Ve a la sección **Images** y busca `postgres`.
3. Descarga la imagen `postgres:17-alpine`.
4. Haz clic en **Run** y configura las siguientes variables de entorno:
   - `POSTGRES_DB` → `incidencias_db`
   - `POSTGRES_USER` → `admin`
   - `POSTGRES_PASSWORD` → `admin123`
5. En la sección de puertos, mapea `5432` (host) → `5432` (contenedor).
6. Asigna el nombre `postgres-incidencias` al contenedor.
7. Haz clic en **Run**.

</details>

### 3. Crear y configurar el archivo `.env`

Copia el archivo de ejemplo:

**Linux / macOS:**
```bash
cp .env.example .env
```

**Windows (CMD):**
```cmd
copy .env.example .env
```

**Windows (PowerShell):**
```powershell
Copy-Item .env.example .env
```

Edita el archivo `.env` con los valores que coincidan con tu contenedor de PostgreSQL:

```properties
# ── Base de Datos ──────────────────────────────────────────
DB_URL=jdbc:postgresql://localhost:5432/incidencias_db
DB_USERNAME=admin
DB_PASSWORD=admin123
JPA_HIBERNATE_DDL_AUTO=update

# ── JWT (Seguridad) ───────────────────────────────────────
JWT_SECRET=mi-clave-secreta-jwt-de-al-menos-32-caracteres!!
JWT_REFRESH_SECRET=mi-clave-secreta-refresh-de-al-menos-32-chars!!

# ── Documentación (Swagger) ───────────────────────────────
APP_DOCS_PUBLIC_ENABLED=true

# ── CORS ──────────────────────────────────────────────────
CORS_ALLOWED_ORIGINS=http://localhost:4200,http://localhost:5500
CORS_ALLOWED_METHODS=GET,POST,PUT,PATCH,DELETE,OPTIONS
CORS_ALLOWED_HEADERS=*
CORS_EXPOSED_HEADERS=Authorization
CORS_ALLOW_CREDENTIALS=true
CORS_MAX_AGE=3600
```

> [!IMPORTANT]
> Las claves `JWT_SECRET` y `JWT_REFRESH_SECRET` deben tener **al menos 32 caracteres**. Para desarrollo local puedes usar cualquier cadena larga, pero **nunca uses las mismas claves en producción**.

> [!WARNING]
> El archivo `.env` contiene credenciales sensibles y **no debe subirse al repositorio**. Ya está incluido en el `.gitignore`.

### 4. Ejecutar la API

Asegúrate de que el contenedor de PostgreSQL esté corriendo antes de iniciar la API.

**Linux / macOS:**
```bash
# Dar permisos de ejecución (solo la primera vez)
chmod +x gradlew

# Ejecutar la API
./gradlew bootRun
```

**Windows (CMD o PowerShell):**
```cmd
gradlew.bat bootRun
```

Si todo está correctamente configurado, deberías ver un log similar a:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v3.5.0)

...
Started BackendIncidenciasApplication in X.XXX seconds
```

### Comandos útiles de Docker (PostgreSQL manual)

```bash
# Ver el estado del contenedor
docker ps

# Detener el contenedor
docker stop postgres-incidencias

# Iniciar el contenedor (si ya fue creado)
docker start postgres-incidencias

# Eliminar el contenedor
docker rm -f postgres-incidencias

# Conectarse a la base de datos desde terminal
docker exec -it postgres-incidencias psql -U admin -d incidencias_db
```

> [!TIP]
> Si necesitas cambiar el puerto por defecto (8080), puedes agregar `SERVER_PORT=9090` en tu archivo `.env` o pasarlo como argumento: `./gradlew bootRun --args='--server.port=9090'`.

---

## ✅ Verificar que Funciona

Una vez que la API esté corriendo (por cualquiera de las dos opciones), verifica que todo funcione correctamente.

### Health Check

```bash
curl http://localhost:8080/swagger-ui/index.html
```

### Desde el navegador

| Recurso | URL |
|---|---|
| Swagger UI | [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html) |
| OpenAPI JSON | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |

Si ves la interfaz de Swagger, ¡la API está funcionando! 🎉

> [!NOTE]
> La documentación de Swagger solo estará disponible si `APP_DOCS_PUBLIC_ENABLED=true` en tu configuración.

---

## 📖 Documentación de la API

La API cuenta con documentación interactiva generada automáticamente mediante **SpringDoc OpenAPI (Swagger UI)**.

Desde la interfaz de Swagger podrás:

- 📄 Ver todos los endpoints disponibles
- 🧪 Probar las peticiones directamente desde el navegador
- 🔐 Autenticarte con JWT para acceder a endpoints protegidos
- 📦 Ver los esquemas de request/response

---

## 🏗️ Estructura del Proyecto

```
backend-incidencias/
├── src/
│   ├── main/
│   │   ├── java/com/utp/backend_incidencias/
│   │   │   ├── auth/            # Autenticación (login, registro, JWT)
│   │   │   ├── common/          # Utilidades y clases compartidas
│   │   │   ├── incident/        # Módulo de incidencias
│   │   │   ├── schoolclass/     # Módulo de clases/aulas
│   │   │   ├── security/        # Configuración de seguridad
│   │   │   ├── student/         # Módulo de estudiantes
│   │   │   ├── user/            # Módulo de usuarios
│   │   │   └── BackendIncidenciasApplication.java
│   │   └── resources/
│   │       └── application.yaml # Configuración de la aplicación
│   └── test/                    # Tests
├── .dockerignore                # Archivos excluidos del build de Docker
├── .env.example                 # Plantilla de variables de entorno
├── build.gradle                 # Dependencias y configuración de Gradle
├── docker-compose.yml           # Docker Compose (PostgreSQL + API)
├── Dockerfile                   # Imagen Docker de la API
├── gradlew                      # Gradle Wrapper (Linux/macOS)
├── gradlew.bat                  # Gradle Wrapper (Windows)
└── README.md                    # Este archivo
```

---

## 🛠️ Tecnologías Utilizadas

| Tecnología | Versión | Uso |
|---|---|---|
| **Java** | 21 | Lenguaje de programación |
| **Spring Boot** | 3.5.0 | Framework principal |
| **Spring Security** | 6.x | Autenticación y autorización |
| **Spring Data JPA** | 3.x | Acceso a datos (ORM) |
| **PostgreSQL** | 17 | Base de datos relacional |
| **JJWT** | 0.12.x | Generación y validación de tokens JWT |
| **Lombok** | — | Reducción de boilerplate |
| **SpringDoc OpenAPI** | 2.8.6 | Documentación Swagger UI |
| **Gradle** | 8.10 | Sistema de build |
| **Docker** | 20+ | Contenedorización |

---

## 🐛 Solución de Problemas

### Error: `Connection refused` al iniciar la API

- Verifica que el contenedor de PostgreSQL esté corriendo: `docker ps`
- Confirma que el puerto `5432` no esté siendo usado por otra aplicación
- **Docker Compose:** Asegúrate de que el health check de postgres haya pasado: `docker compose ps`
- **Inicio manual:** Revisa que los valores de `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` en el `.env` coincidan con los del contenedor Docker

### Error: `Permission denied` al ejecutar `./gradlew`

```bash
chmod +x gradlew
```

### Error: `JAVA_HOME is not set`

> Solo aplica para el inicio manual. Docker Compose no requiere Java instalado.

Configura la variable de entorno `JAVA_HOME` apuntando a tu instalación de Java 21:

**Linux / macOS:**
```bash
export JAVA_HOME=/ruta/a/tu/jdk-21
```

**Windows:**
```cmd
set JAVA_HOME=C:\Program Files\Java\jdk-21
```

> [!TIP]
> Para hacer permanente la variable `JAVA_HOME`, agrégala a tu archivo `~/.bashrc` (Linux), `~/.zshrc` (macOS) o en las Variables de Entorno del Sistema (Windows).

### Error: `The specified port is already in use`

Otro proceso está usando el puerto 8080. Opciones:

1. Encuentra y detén el proceso que ocupa el puerto:
   ```bash
   # Linux/macOS
   lsof -i :8080
   kill -9 <PID>

   # Windows
   netstat -ano | findstr :8080
   taskkill /PID <PID> /F
   ```
2. Cambia el puerto de la API:
   - **Docker Compose:** Modifica el mapeo de puertos en `docker-compose.yml`: `"9090:8080"`
   - **Inicio manual:** Agrega `SERVER_PORT=9090` en tu `.env`

### Error: `JWT secret key must be at least 32 characters`

Las claves `JWT_SECRET` y `JWT_REFRESH_SECRET` son demasiado cortas. Asegúrate de usar cadenas de al menos 32 caracteres.

### Docker Compose: Reconstruir después de cambios en el código

Si modificaste el código fuente y necesitas que Docker Compose lo refleje:

```bash
docker compose up -d --build
```

---

## 📝 Resumen Rápido

### Con Docker Compose (recomendado)

```bash
git clone https://github.com/tu-usuario/backend-incidencias.git
cd backend-incidencias
docker compose up -d
# Abrir http://localhost:8080/swagger-ui/index.html
```

### Inicio manual

```bash
git clone https://github.com/tu-usuario/backend-incidencias.git
cd backend-incidencias

# Levantar PostgreSQL
docker run -d --name postgres-incidencias \
  -e POSTGRES_DB=incidencias_db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin123 \
  -p 5432:5432 postgres:17-alpine

# Configurar el .env
cp .env.example .env
# Editar .env con tus valores

# Ejecutar la API
chmod +x gradlew        # Solo Linux/macOS, solo la primera vez
./gradlew bootRun       # Linux/macOS
# gradlew.bat bootRun   # Windows

# Abrir http://localhost:8080/swagger-ui/index.html
```

---

<p align="center">
  Desarrollado con ☕ y Spring Boot
</p>
