# Wizard School Backend

A Spring Boot 3.5 service (Java 21, Gradle) that manages student applications, enrolments and specialisations for the Wizardry School demo project.  The repo is optimised for **zero‑friction onboarding**—one command launches a local PostgreSQL 16 database **and** the backend on Windows 10/11, macOS 14+, or Linux.

---

## Table of Contents

1. [Tech Stack](#tech-stack)
2. [Prerequisites](#prerequisites)
3. [Quick Start](#quick-start)
4. [Daily Workflow](#daily-workflow)
5. [Database Lifecycle](#database-lifecycle)
6. [Running Tests](#running-tests)
7. [API Cheat‑Sheet](#api-cheat-sheet)
8. [Troubleshooting](#troubleshooting)

---

## Tech Stack

| Layer      | Choice                             | Why                                   |
| ---------- | ---------------------------------- | ------------------------------------- |
| Language   | **Java 21 LTS**                    | Modern records, virtual threads       |
| Framework  | **Spring Boot 3.5.3**              | Production features out‑of‑the‑box    |
| Build      | **Gradle 8 (wrapper)**             | Fast, IDE‑friendly, Kotlin/Groovy DSL |
| Database   | **PostgreSQL 16** (Docker)         | Cross‑platform, battle‑tested         |
| Migrations | SQL seed scripts → optional Flyway | Students see raw DDL first            |
| Testing    | JUnit 5, Testcontainers            | Re‑use same Postgres image in CI      |

---

## Prerequisites

| Tool                                   | Version       | Install Guide                                                                                    |
| -------------------------------------- | ------------- | ------------------------------------------------------------------------------------------------ |
| **JDK 21**                             | 21.0.x        | OpenJDK, Oracle, Azul… Set `JAVA_HOME`.                                                          |
| **Docker Desktop**                     | 4.31 or newer | [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop) |
| **Git**                                | Any           | Clone the repo                                                                                   |
| *(Optional)* **IntelliJ IDEA 2024.1+** | —             | Recognises Gradle tasks automatically                                                            |

> **Windows note** – enable WSL 2 backend for best Docker performance.

---

## Quick Start

```bash
# 1 Clone
$ git clone https://github.com/your‑org/wizard-school-backend.git
$ cd wizard-school-backend

# 2 Launch everything (DB + backend, dev profile)
$ ./gradlew bootRun          # Windows: gradlew.bat bootRun
```

Gradle will:

1. **Compose Up** the `wizard_db` Postgres container (first run pulls the image).
2. Execute the SQL scripts in `db/init/` to create & seed the schema.
3. Start Spring Boot on [http://localhost:8080](http://localhost:8080).

Open [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health) to verify.

---

## Daily Workflow

| Task                                       | Command                                       |
| ------------------------------------------ | --------------------------------------------- |
| Start backend + DB                         | `./gradlew bootRun`                           |
| Custom dev task *(skips build scans etc.)* | `./gradlew dev`                               |
| Stop everything                            | `Ctrl‑C` then `docker compose down`           |
| *Reset* database to factory state          | `docker compose down -v && ./gradlew bootRun` |
| Build JAR (CI/CD)                          | `./gradlew bootJar`                           |

IntelliJ shows **bootRun** and **dev** as green ▶ run icons under *Gradle Tool Window → Tasks → application*.

---

## Database Lifecycle

* **Image** – `postgres:16`.
* **Config file** – `docker-compose.yml` (checked into repo).
* **Initialisation** – SQL files in `db/init/` run **once** per fresh volume:
  `01_schema.sql`, `02_seed_specializations.sql`, `03_demo_data.sql` (optional).
* **Credentials (dev)**
  `jdbc:postgresql://localhost:5432/wizard_school`   |  user `wizard`   |  password `secret`
* **Migrations** – add Flyway later via Gradle dependency if desired.

---

## Running Tests

```bash
# Unit + slice + integration (Testcontainers Postgres)
$ ./gradlew test
```

Testcontainers spins up a disposable database; Docker must be running but the dev container is not required.

---

## API Cheat‑Sheet

| Verb | Endpoint                                                  | Purpose                                     |
| ---- | --------------------------------------------------------- | ------------------------------------------- |
| POST | `/api/submissions`                                        | Create an application or withdrawal request |
| GET  | `/api/submissions/search?ssn=…`                           | Search submissions by SSN                   |
| GET  | `/api/submissions/search?specialization=ARCANE&year=2026` | Search by spec & year                       |
| GET  | `/api/submissions/{id}`                                   | Application details                         |
| GET  | `/api/enrollments/search?...`                             | Search enrolments                           |
| GET  | `/api/students/{id}`                                      | Student record                              |

All endpoints return **JSON**; standard Spring Boot error format on failures.

---

## Troubleshooting

| Symptom                                                                   | Fix                                                                                                       |
| ------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------- |
| `A problem occurred starting process 'command 'docker''`                  | IDE/Gradle can’t locate Docker CLI.                                                                       |
|   • Restart IDE launched from a new terminal *after* Docker install.      |                                                                                                           |
|   • Or set `DOCKER_BIN` env‑var / `-PdockerExecutable=/full/path/docker`. |                                                                                                           |
| `Connection refused localhost:5432`                                       | Run `docker compose ps` – if `wizard_db` is **exited** run `docker compose up -d` then retry.             |
| IDE says "unknown JNI method"                                             | Using wrong JDK.                                                                                          |
|   Run `java -version` → should print 21.x.                                |                                                                                                           |
| Port 8080 busy                                                            | Change `server.port` in `src/main/resources/application.yml` or use `SERVER_PORT=9090 ./gradlew bootRun`. |

---

## Contributing

1. Fork → feature branch → PR.
2. Ensure `./gradlew test` passes.
3. Follow existing code style; use Lombok or records for DTOs.

---

## License

MIT © 2025 Wizardry School contributors
