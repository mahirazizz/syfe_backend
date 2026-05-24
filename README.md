# Personal Finance Manager

Minimal Spring Boot implementation (Java 25) with H2 for development and a Dockerfile ready for Render deployment.

Quick start (development):

Requirements:

- Java 25 installed and `JAVA_HOME` pointing to it
- Maven installed (or use the project `mvnw` wrapper if present)

Run (PowerShell example):

```powershell
$env:JAVA_HOME='C:\Path\To\jdk-25'
$env:PATH="$env:JAVA_HOME\bin;C:\path\to\maven\bin;$env:PATH"
mvn spring-boot:run
```

Run on a different port (PowerShell - quoted property):

```powershell
mvn spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

If you prefer the system wrapper and it exists in the project, use `mvnw.cmd spring-boot:run` on Windows.

H2 console: http://localhost:8080/h2-console (JDBC URL: `jdbc:h2:mem:financedb`)

API notes:

- Registration: `POST /api/auth/register`
- Login: `POST /api/auth/login` (session cookie)
- Logout: `POST /api/auth/logout`

Deploy to Render using Docker: create a new Web Service with Dockerfile and expose port 8080.

Troubleshooting

- If you see "Port 8080 is already in use", either stop the process using that port or run the app on another port. To find and stop the process on Windows PowerShell:

```powershell
$pid = (Get-NetTCPConnection -LocalPort 8080).OwningProcess
Stop-Process -Id $pid -Force
```

Or run the app on another port as shown above.
