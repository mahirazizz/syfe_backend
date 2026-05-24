# Personal Finance Manager
Spring Boot implementation for the Personal Finance Manager assignment.

## Status

- Features implemented:
	- User registration with `email`, `password`, `fullName`, `phoneNumber` (`POST /api/auth/register`).
	- Session-based login (`POST /api/auth/login`) storing `USER_ID` in server session and logout (`POST /api/auth/logout`).
	- Session interceptor enforcing authentication for `/api/**` endpoints (except `/api/auth/**`).
	- Transactions CRUD: create, read (user-scoped), update (cannot change date), soft-delete (excluded from reports/goals). Date validation prevents future dates.
	- Categories: seeded default non-deletable categories (Salary, Food, Rent, Transportation, Entertainment, Healthcare, Utilities). Users can create custom categories (unique per user) and delete them if not referenced by transactions.
	- Savings goals: create/update/delete; progress calculation implemented (income - expenses since goal `startDate`) and `GoalResponse` contains `currentProgress`, `progressPercentage`, and `remainingAmount`.

- Work in progress / TODO:
	- Reports aggregation (monthly/yearly) is scaffolded (DTOs added) but numeric aggregation implementation is pending.
	- Unit tests and coverage (required 80%) are not yet implemented.
	- Some API response shapes may be simplified; see API section below.

## Stack

- Java 17
- Spring Boot 3.x
- Spring Web
- Spring Data JPA
- Spring Security (session-based via interceptor)
- H2 Database for development

## Run locally

Requirements: JDK 17+, Maven

Start the app:

```bash
mvn spring-boot:run
```

H2 console: http://localhost:8080/h2-console (JDBC URL from application.properties)

Run tests (if added):

```bash
mvn test
```

## API (implemented endpoints)

- `POST /api/auth/register` — Register a user. Request JSON: `{ "email":"user@example.com", "password":"pwd", "fullName":"Name", "phoneNumber":"+123" }`. Returns 201 on success.
- `POST /api/auth/login` — Login. Request JSON: `{ "email":"user@example.com", "password":"pwd" }`. On success returns 200 and sets a session cookie.
- `POST /api/auth/logout` — Logout (invalidates session).

- `GET /api/transactions` — Returns logged-in user's transactions (newest first).
- `POST /api/transactions` — Create transaction. Request JSON: `{ "amount":100.0, "description":"desc", "type":"INCOME|EXPENSE", "transactionDate":"2024-01-01", "categoryId":1 }`.
- `PUT /api/transactions/{id}` — Update transaction fields except date.
- `DELETE /api/transactions/{id}` — Soft-delete transaction.

- `GET /api/categories` — Get categories (default + user's custom).
- `POST /api/categories` — Create custom category for user.
- `DELETE /api/categories/{name}` — Delete a custom category by name if not referenced.

- `GET /api/goals` — List goals for the logged-in user (includes progress fields).
- `POST /api/goals` — Create goal. Request JSON: `{ "name":"Emergency", "targetAmount":5000.0, "currentAmount":0.0, "targetDate":"2026-01-01" }`.
- `PUT /api/goals/{id}` — Update goal target/amount/date.
- `DELETE /api/goals/{id}` — Delete goal.

- `GET /api/reports/monthly` — (Planned) Monthly report aggregation; query params `year` and `month`.
- `GET /api/reports/yearly/{year}` — (Planned) Yearly report aggregation.

## Notes & Design Decisions

- Authentication: implemented via server HTTP session and a small `SessionAuthInterceptor` to protect endpoints. This meets the assignment requirement for session-based auth with cookies.
- Data isolation: all data queries are scoped to the logged-in `USER_ID` stored in session; controllers derive user from session.
- Transactions: soft-delete implemented via `deleted` flag so removed transactions do not affect reports or goals.
- Categories: seeded defaults are non-custom. Custom categories are tied to users and must be unique per user.
- Goals: progress is computed from all non-deleted transactions of the user since the goal's `startDate`.

## What remains to reach full spec

1. Implement monthly/yearly report numeric aggregation and ensure output matches the provided test script.
2. Add comprehensive unit tests to reach 80% coverage and include mocking where appropriate.
3. Tweak API shapes to exactly match the assignment JSON responses if strict parity is required by the test harness.
4. Add deployment configuration (Render) and verify the provided test script passes.

If you want, I can finish the reports implementation and add unit tests next — would you like me to proceed with reports or tests first?
