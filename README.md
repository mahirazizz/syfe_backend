# Personal Finance Manager

Minimal Spring Boot implementation (Java 25) with H2 for development and a Dockerfile ready for Render deployment.

Quick start (development):

```bash
mvn spring-boot:run
```

H2 console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:financedb`)

API notes:
- Registration: `POST /api/auth/register`
- Login: `POST /api/auth/login` (session cookie)
- Logout: `POST /api/auth/logout`

Deploy to Render using Docker: create a new Web Service with Dockerfile and expose port 8080.
