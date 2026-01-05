# 📌 Sistema de Turnos - Spring Boot + MySQL + WebSocket + Docker

Este proyecto implementa un **sistema de gestión de turnos** utilizando **Spring Boot**, **MySQL**, **WebSocket** y **Docker Compose**.  
Permite registrar personas, generar tickets de atención, asignarlos a módulos y consultar el tablero de últimos llamados.

## Requisitos
- Java 21
- Maven 3.9+
- Docker (opcional, recomendado)
---

## 🚀 Despliegue con Docker

### 1. Clonar el repositorio
```bash
git clone https://github.com/juandariasgomez/turnos-system.git
cd sistema-turnos
```

## 2) Levantar MySQL con Docker
```bash
docker compose up -d
```

Credenciales por defecto (ver `docker-compose.yml`):
- DB: `turnos`
- user: `root`
- pass: `root_password`

## 3) Ver logs
```bash
docker-compose logs -f
```

## 4) Parar contenedores
```bash
docker-compose down
```

## 5) Configurar aplicación
`src/main/resources/application.yml` ya viene listo para usar variables de entorno:
```
DB_HOST: mysql
DB_PORT: 3306
DB_NAME: turnos
DB_USER: root
DB_PASS: root_password
```
Si corres la app local sin Docker, se conecta a:
`jdbc:mysql://localhost:3306/turnos
usuario: root
clave: root_password`

## 6) Ejecutar
```bash
mvn spring-boot:run
```

## 7) Probar en el navegador
- Tablero (TV): http://localhost:8080/index.html
- Cajero: http://localhost:8080/cajero.html
- Crear Ticket: http://localhost:8080/ticket.html
- Swagger: http://localhost:8080/swagger-ui.html

## 8) Flujo de prueba (sin Angular)
1. **Mock RENIEC**  
   `POST /api/reniec/lookup/12345678`
2. **Crear ticket**  
   `POST /api/tickets` (Body: JSON con los campos del `PersonDTO`)
3. **Llamar siguiente**  
   `GET /api/tickets/next?module=1`
4. **Terminar atención**  
   `GET /api/tickets/2/serve`
5. **Buscar personas por DNI**  
   `GET /api/persons/searchByDNI/78019778`
6. **Ver tablero** (se actualiza en tiempo real por WebSocket)

## Estructura
- `person/` entidad y repo de personas (DNI).
- `ticket/` entidad, repo y servicio de tickets.
- `api/` controladores REST + DTOs.
- `config/` WebSocket y CORS.

## Notas
- El consumo RENIEC es **mock** para la demo. Implementa un `ReniecClient` real con `WebClient` y credenciales si vas a producción.
- Si usas Angular, apúntalo a `http://localhost:8080` y habilita CORS (ya está).
