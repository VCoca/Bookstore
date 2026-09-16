# Bookstore API

REST API for browsing and purchasing books — Spring Boot 4, PostgreSQL, JWT authentication.

![Tests](https://github.com/VCoca/Bookstore/actions/workflows/tests.yml/badge.svg)

## What it does

Users register, browse the book catalogue and buy books. Each purchase decrements
the available stock, charges the amount through a payment service, records an order
and sends a confirmation email. Users can view their own purchase history.

Administrators manage the catalogue (add, update, delete books) and can see all orders.

## Running it

Docker is required.

```bash
git clone https://github.com/VCoca/Bookstore.git
cd Bookstore
cp .env.example .env
docker compose up --build
```

Set `DB_PASSWORD` and `JWT_SECRET` in `.env` (any string of at least 32 characters).

| What | Where |
|---|---|
| API | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI spec | http://localhost:8080/v3/api-docs |
| Mailpit (sent emails) | http://localhost:8025 |

### Test accounts

| Role | Email | Password |
|---|---|---|
| ADMIN | admin@bookstore.local | admin1234 |
| USER | ivan@bookstore.local | user1234 |

The catalogue is seeded with five books, one of which deliberately has zero copies
left — useful for testing the 409 response on purchase.

### Authentication

`POST /api/auth/login` returns a JWT. Send it as `Authorization: Bearer <token>` on
every protected endpoint. Tokens are valid for 24 hours.

## Tests

```bash
./mvnw test      # unit tests and web layer — a few seconds
./mvnw verify    # plus integration tests (requires Docker)
```

## Stack

Java 21 · Spring Boot 4.1 · Spring Security · Spring Data JPA · Hibernate ·
PostgreSQL 16 · Flyway · springdoc-openapi · JUnit 5 · Mockito · Testcontainers ·
Docker Compose · GitHub Actions

## Design decisions

### Atomic stock decrement

Checking stock and then decrementing it in two steps lets two concurrent buyers
both pass the check and buy the last copy. Instead, a conditional UPDATE is used,
and the number of affected rows is what decides:

```sql
UPDATE books SET available_copies = available_copies - 1
WHERE isbn = :isbn AND available_copies > 0
```

Zero rows updated means no copies are left, and the service throws
`NoMoreBooksException`, which maps to 409. The database also carries
`CHECK (available_copies >= 0)` as a last line of defence independent of the code.

### Payment before persisting the order

The order inside `buyBook` is: decrement stock → charge → save the order. If the
charge fails, the transaction rolls back and the copy returns to stock.

The reverse does not hold — charging is an external call and cannot be undone by a
database rollback. That is why `transactionId` is stored with the order, as a trail
for any dispute. The payment service sits behind a `PaymentClient` interface, so
tests substitute a mock and development uses a fake implementation that declines
charges above a threshold.

### Email outside the transaction

The confirmation email is sent through a `@TransactionalEventListener` with the
`AFTER_COMMIT` phase, so it only goes out once the transaction has actually
committed — not when it is later rolled back.

### Flyway instead of ddl-auto

The database schema lives in versioned SQL migrations, and `ddl-auto=validate`
verifies that the entities match it on every startup. `ddl-auto=update` cannot
rename a column or transform existing data, and leaves different schemas on
different machines.

Seed data sits in a separate folder (`db/dev`) loaded only under the `local`
profile, so it never reaches production.

### Identity from the token, never from the request

`GET /api/orders/me` takes the email from the `Authentication` object, not from a
parameter. If it were a parameter, anyone could read someone else's orders by
changing it.

### Three layers of tests

| Layer | What it verifies | Duration |
|---|---|---|
| Unit (Mockito) | service decisions — which exceptions it throws, what it calls and what it doesn't | milliseconds |
| `@WebMvcTest` | the HTTP contract — validation, status codes, error format | seconds |
| `@SpringBootTest` + Testcontainers | consequences in a real database — rollback, atomic UPDATE | ~20s |

Integration tests (`*IT`) are separated by the Failsafe plugin, so `mvn test` stays
fast for everyday work. They run against a real PostgreSQL in a Docker container,
which also verifies that the migrations work on an empty database.

### RFC 7807 for errors

Every error returns a `ProblemDetail`. Validation errors additionally carry a map
keyed by field, so the client knows exactly what is wrong:

```json
{
  "title": "Validation failed",
  "status": 400,
  "errors": { "isbn": "ISBN must be exactly 13 digits" }
}
```

## Structure

Packages are organised by feature (`book`, `user`, `order`, `payment`, `security`)
rather than by layer. Each package holds the controller, service, repository, DTOs
and exceptions for its domain.

Endpoint documentation is extracted into separate interfaces (`BookApi`, `OrderApi`,
`UserApi`) to keep the controllers readable.
