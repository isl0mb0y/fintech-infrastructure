# Payming

A learning project: a Payme/Click-style payment system for Uzbekistan.
Users link Humo and Uzcard cards, transfer money card-to-card (P2P), pay for
services (utilities, telecom), and get transaction statements. Money stays on
the bank card; the system orchestrates processing centers and keeps a
double-entry ledger. A wallet is planned as a later phase on top of the same
ledger.

Real Uzcard/Humo APIs are not accessible without a contract, so the project
ships its own `mock-processing` service that imitates both networks,
including a chaos mode (latency, timeouts, errors) for reliability testing.

## Modules

| Module            | Port | Description                                            |
|-------------------|------|--------------------------------------------------------|
| `mock-processing` | 8081 | Fake Uzcard/Humo processing center: cards, holds, OTP  |
| `payming-core`    | 8080 | Main payment system (coming in phase 1)                |

Modules talk to each other over HTTP only and never depend on each other's code.

## Stack

Java 21, Spring Boot 3, Maven (multi-module), PostgreSQL, Docker Compose.
Kafka, Redis, ClickHouse, gRPC and Kubernetes are added in later phases.

## Getting started

```bash
docker compose up -d        # PostgreSQL
./mvnw -pl mock-processing spring-boot:run
```

Swagger UI: http://localhost:8081/swagger-ui.html

## Tests

```bash
./mvnw test
```