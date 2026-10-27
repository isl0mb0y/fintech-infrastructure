# Fintech Infrastructure

A learning project that models the fintech infrastructure behind card payments
in Uzbekistan: a payment service provider (Payme/Click role), a card processing
network (Uzcard/Humo role) and a core banking system. Each system is a separate
Spring Boot application with its own PostgreSQL database; they talk to each
other over HTTP only.

```
 User ──▶ psp ──▶ card-processing ──▶ core-banking
          PSP      payment network      bank accounts
                                        and ledger
```

## Modules

| Module            | Port | Role                                                                 | Status      |
|-------------------|------|----------------------------------------------------------------------|-------------|
| `card-processing` | 8081 | Card network: card binding with OTP, holds, credits, daily registry   | in progress |
| `psp`             | 8080 | Payment service provider: P2P transfers, bill payments, statements    | planned     |
| `core-banking`    | 8082 | Bank: customers, accounts, double-entry ledger, card issuing          | planned     |

Modules never depend on each other's code. `card-processing` also has a chaos
mode (latency, timeouts, errors) for reliability testing of its callers.

## Stack

Java 21, Spring Boot 3, Maven (multi-module), PostgreSQL, Liquibase,
OpenAPI (spec-first), Docker Compose. Kafka, Redis, ClickHouse, gRPC and
Kubernetes are added in later phases.

## Getting started

```bash
docker compose up -d                          # databases
./mvnw -pl card-processing spring-boot:run    # card-processing on :8081
```

Swagger UI: http://localhost:8081/swagger-ui.html

### Test cards (card-processing)

Created on first start. OTP is always `123456`.

| PAN                | Expiry | Phone           | Bank    | Note          |
|--------------------|--------|-----------------|---------|---------------|
| `8600123412341234` | 12/28  | `+998901234567` | KAPITAL |               |
| `8600111122223333` | 06/27  | `+998901234568` | KAPITAL |               |
| `9860123412341234` | 03/29  | `+998901234569` | IPOTEKA |               |
| `9860111122223333` | 01/28  | `+998901234570` | HAMKOR  | low balance   |
| `8600999988887777` | 12/28  | `+998901234571` | IPOTEKA | blocked       |
| `9860999988887777` | 01/24  | `+998901234572` | HAMKOR  | expired       |

## Tests

```bash
./mvnw test
```
