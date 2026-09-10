# Customers Backend

Microservicio REST para la gestión de clientes.

La aplicación permite crear clientes, consultar clientes con filtros opcionales por DNI o email y obtener indicadores relacionados con la fecha de nacimiento de los clientes registrados.

## Tecnologías

- Java 17
- Spring Boot
- Maven
- Spring Web
- Spring Data JPA
- Bean Validation
- H2 Database
- JUnit 5
- Mockito

## Requisitos

Para ejecutar el proyecto es necesario tener instalado:

- Java 17 o superior

No es necesario instalar Maven, ya que el proyecto incluye Maven Wrapper.

## Instalación

Clonar el repositorio:

```bash
git clone <URL_DEL_REPOSITORIO>
cd customers-backend
```

## Ejecución

### Windows

```bash
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en:

```
http://localhost:8080
```

## Base de datos

El proyecto utiliza H2 en memoria para facilitar la ejecución local y la evaluación técnica.

Configuración principal:

- **JDBC URL:** `jdbc:h2:mem:customersdb`
- **User:** `sa`
- **Password:** *(vacío)*

La consola H2 está disponible en:

```
http://localhost:8080/h2-console
```

Al utilizar una base de datos en memoria, los datos se eliminan cuando la aplicación se detiene o reinicia.

## Modelo de Cliente

Un cliente contiene los siguientes campos:

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | Long | Identificador generado automáticamente |
| `nombre` | String | Nombre del cliente |
| `apellido` | String | Apellido del cliente |
| `email` | String | Email único |
| `dni` | String | DNI único de 8 dígitos |
| `fechaCreacion` | LocalDateTime | Fecha generada automáticamente por el backend |
| `fechaNacimiento` | LocalDate | Fecha de nacimiento |

> `fechaCreacion` no se recibe desde el cliente. Es generada automáticamente al registrar un nuevo cliente.

## API

**Base URL:**

```
http://localhost:8080/api/customers
```

### Crear cliente

```
POST /api/customers
```

Ejemplo:

```json
{
  "nombre": "John",
  "apellido": "Doe",
  "email": "john.doe@email.com",
  "dni": "12345678",
  "fechaNacimiento": "1995-04-15"
}
```

Respuesta exitosa: `201 Created`

Ejemplo de respuesta:

```json
{
  "id": 1,
  "nombre": "John",
  "apellido": "Doe",
  "email": "john.doe@email.com",
  "dni": "12345678",
  "fechaCreacion": "2026-09-10T17:23:22",
  "fechaNacimiento": "1995-04-15"
}
```

### Consultar todos los clientes

```
GET /api/customers
```

Retorna todos los clientes registrados.

### Consultar por DNI

```
GET /api/customers?dni=12345678
```

### Consultar por email

```
GET /api/customers?email=john.doe@email.com
```

### Consultar por DNI y email

```
GET /api/customers?dni=12345678&email=john.doe@email.com
```

Los filtros son opcionales.

Cuando una búsqueda no encuentra resultados, se retorna:

```json
[]
```

con estado: `200 OK`

### Indicadores

```
GET /api/customers/indicadores
```

El endpoint retorna:

- Cantidad de clientes nacidos por mes/año.
- Mes/año con mayor cantidad de clientes nacidos.
- Mes/año con menor cantidad de clientes nacidos.
- Tasa de natalidad de cada mes/año.

Ejemplo:

```json
{
  "natalidadPorMesAnio": [
    {
      "mes": 5,
      "anio": 1994,
      "cantidad": 2,
      "tasaNatalidad": 33.33
    },
    {
      "mes": 4,
      "anio": 1995,
      "cantidad": 1,
      "tasaNatalidad": 16.67
    }
  ],
  "mesAnioConMayorNatalidad": {
    "mes": 5,
    "anio": 1994,
    "cantidad": 2,
    "tasaNatalidad": 33.33
  },
  "mesAnioConMenorNatalidad": {
    "mes": 4,
    "anio": 1995,
    "cantidad": 1,
    "tasaNatalidad": 16.67
  }
}
```

### Cálculo de tasa de natalidad

Para esta implementación se define como el porcentaje de clientes nacidos en cada mes/año respecto al total de clientes registrados:

```
(clientes nacidos en el mes/año / total de clientes registrados) x 100
```

El resultado se expresa como porcentaje y se redondea a dos decimales.

En caso de empate para el mayor o menor número de nacimientos, se retorna el primer mes/año según el orden cronológico utilizado por la aplicación.

## Validaciones

Durante la creación de clientes se validan las siguientes reglas:

- Nombre obligatorio.
- Apellido obligatorio.
- Email obligatorio y con formato válido.
- DNI obligatorio y compuesto por exactamente 8 dígitos.
- Fecha de nacimiento obligatoria.
- La fecha de nacimiento no puede ser futura.
- DNI único.
- Email único.

### Error de validación

Las validaciones de entrada retornan: `400 Bad Request`

Ejemplo:

```json
{
  "status": 400,
  "message": "Error de validación",
  "errors": {
    "email": "El email debe tener un formato válido",
    "dni": "El DNI debe contener exactamente 8 dígitos"
  },
  "timestamp": "2026-09-10T17:30:00"
}
```

### Cliente duplicado

Un DNI o email ya registrado retorna: `409 Conflict`

Ejemplo:

```json
{
  "status": 409,
  "message": "Ya existe un cliente con el DNI: 12345678",
  "timestamp": "2026-09-10T17:30:00"
}
```

## Arquitectura

El proyecto utiliza una arquitectura por capas:

```
src/main/java/com/example/customers
├── controller
├── dto
├── entity
├── exception
├── repository
├── service
└── CustomersApplication.java
```

Responsabilidades principales:

- **controller:** exposición de la API REST.
- **service:** lógica de negocio.
- **repository:** acceso a datos mediante Spring Data JPA.
- **entity:** modelo de persistencia.
- **dto:** contratos de entrada y salida de la API.
- **exception:** manejo centralizado de errores.

Se eligió una arquitectura en capas debido al alcance acotado del microservicio, evitando introducir complejidad innecesaria.

## Tests

Los tests unitarios utilizan JUnit 5 y Mockito.

Actualmente se cubren comportamientos críticos del servicio:

- Creación exitosa de clientes.
- Rechazo de clientes con DNI duplicado.
- Cálculo de indicadores por mes/año.

Para ejecutar los tests:

### Windows

```bash
.\mvnw.cmd test
```

### Linux / macOS

```bash
./mvnw test
```

## Postman

La colección de Postman se encuentra en:

```
postman/customers-api.postman_collection.json
```

La colección incluye requests para:

- Crear clientes.
- Consultar todos los clientes.
- Consultar por DNI.
- Consultar por email.
- Consultar por DNI y email.
- Consultar indicadores.

La variable `baseUrl` tiene como valor por defecto:

```
http://localhost:8080
```

La colección puede importarse directamente desde Postman utilizando la opción **Import**.
