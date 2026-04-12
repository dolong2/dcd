# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

DCD is a Spring Boot REST API built with Kotlin that manages containerized applications. It supports creating, deploying, running, and managing Docker-based applications with various frameworks (Spring Boot, Django, etc.). The system uses MariaDB for persistence, Redis for caching/rate limiting, and Docker Java client for container management.

## Architecture

The project uses a **layered architecture** organized as follows:

```
src/main/kotlin/com/dcd/server/
├── core/
│   ├── domain/           # Business logic, use cases, domain models
│   └── common/           # Shared utilities, DTOs, enums
├── persistence/          # Data access layer (repositories, entities)
│   ├── application, auth, user, workspace, volume, env, domain/
│   └── *PersistenceAdapter.kt
├── presentation/         # REST controllers (web adapters)
│   └── domain/
│       ├── application, auth, user, workspace, domain/
│       └── *WebAdapter.kt
└── infrastructure/       # For Spring configuration & third-party integrations
    └── global/
        ├── config/       # Security, WebSocket, Filter, Redis configs
        ├── security/     # JWT, authentication
        ├── jwt/          # Token utilities
        └── thirdparty/   # Docker client, mail, etc.
```

**Key Pattern**: Each domain feature (application, user, auth, workspace, volume) has:
- `*UseCase` interfaces in `core.domain.*.usecase`
- `*PersistenceAdapter` in `persistence` (implements repository pattern)
- `*WebAdapter` (REST controller) in `presentation`
- `*ResDto` / `*ReqDto` for API contracts

## Development Commands

### Building
```bash
./gradlew clean build          # Full build with tests
./gradlew build -x test        # Build without tests
```

### Running Tests
```bash
./gradlew test                 # Run all tests
./gradlew test --tests "*ApplicationWebAdapterTest"  # Run single test class
./gradlew test --tests "*ApplicationWebAdapterTest.createApplication*"  # Run specific test
```

### Running the Application
```bash
# Start infrastructure
docker network create dcd      # Create network (one time)
docker-compose up -d           # Start MariaDB, Redis, Nginx

# Run application
./gradlew bootRun

# Or build and run jar
./gradlew clean build
java -jar build/libs/server-0.0.1-SNAPSHOT.jar
```

The application runs on **port 8081** by default.

### Linting & Code Quality
The project uses Kotlin compiler with strict JSR305 annotations. Gradle build includes Kotlin compilation checks.

## Configuration & Environment

Configuration uses Spring Boot's environment-based approach:

### Required Environment Variables
- **Database**: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `DB_DRIVER`, `DB_HIBERNATE_DIALECT`
- **Redis**: `REDIS_HOST`, `REDIS_PORT`, `REDIS_USERNAME`, `REDIS_PASSWORD`
- **JWT**: `JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET`, `JWT_ACCESS_TIME`, `JWT_REFRESH_TIME`
- **Security**: `AES_SECRET`, `AES_INIT_VECTOR`
- **Mail**: `MAIL_ADDRESS`, `MAIL_PASSWORD` (Gmail SMTP)
- **Docker**: `DOCKER_REGISTRY_URL`, `DOCKER_TOKEN`
- **Paths**: `DOMAIN_CONFIG_PATH`

### Configuration Files
- `src/main/resources/application.yml` - Main Spring Boot config
- `src/main/resources/application-test.yml` - Test profile (generated in CI from secrets)
- `docker-compose.yml` - Infrastructure services
- `dcd-db.env`, `dcd-redis.env` - Service credentials
- `.env.example` files in deployment directories

## Testing Strategy

Tests use **Kotest** (BehaviorSpec style) with **MockK** for mocking:

```kotlin
class ApplicationWebAdapterTest : BehaviorSpec({
    val mockUseCase = mockk<SomeUseCase>()
    val adapter = WebAdapter(mockUseCase)

    given("Setup") {
        `when`("Action") {
            // Arrange
            every { mockUseCase.execute(any()) } returns expectedResult

            // Act
            val result = adapter.someMethod()

            // Assert
            then("Expectation") {
                result shouldBe expected
                verify { mockUseCase.execute(any()) }
            }
        }
    }
})
```

**Test Location Pattern**: Tests mirror source structure in `src/test/kotlin/`

## Key Features & Components

### Authentication & Security
- JWT tokens (access + refresh)
- Spring Security with custom filters
- Located in `infrastructure.global.security` and `infrastructure.global.jwt`

### Database
- **ORM**: Spring Data JPA with Hibernate
- **Dialect**: MySQL (MariaDB compatible)
- **DDL**: `ddl-auto: none` - migrations are manual
- **Entities**: Located in `persistence.*.domain` packages

### Caching & Rate Limiting
- **Caching**: Spring Cache + Redis (Redisson client)
- **Rate Limiting**: bucket4j with Redis backing
- `@EnableCaching` at application startup

### WebSocket Support
- Enabled with `@EnableWebSocket`
- Config in `infrastructure.global.config.WebSocketConfig`
- Used for real-time application logs and status updates

### Docker Integration
- Uses `docker-java` client library
- `DockerClientConfig` manages Docker daemon connection
- Registry authentication via environment variables
- Used for building, pushing, and running containerized applications

### Scheduling
- `@EnableScheduling` at startup
- Background tasks configured in various use cases

## Important Patterns & Conventions

1. **Use Case Pattern**: Domain logic separated into `*UseCase` interfaces/implementations. Web adapters inject and call these.

2. **Data Transfer**: DTOs separate API contracts from domain models.
   - `*ReqDto` for incoming requests
   - `*ResDto` for outgoing responses
   - Extensions (`.toResponse()`) for model-to-DTO conversions

3. **Exception Handling**: Custom exceptions per domain feature under `core.domain.*.exception`

4. **Repository Pattern**: `*PersistenceAdapter` implements the repository interface, abstracting database details

5. **Logging**: Check `logback-spring.xml` for logging configuration

6. **WebSocket Handler Pattern**: Command/listener architecture in `infrastructure.global.socket.command`

## CI/CD

- **CI Workflow** (`.github/workflows/ci.yml`): Runs on each PR to develop/main
  - Builds with Gradle
  - Runs all tests with Redis service
  - Test config from GitHub secrets
- **CD Workflow** (`.github/workflows/cd.yml`): For deployment automation

## Common Development Tasks

### Adding a New Domain Feature
1. Create use case interfaces in `core/domain/{feature}/usecase`
2. Create entity in `persistence/{feature}/domain`
3. Create repository interface and adapter in `persistence/{feature}`
4. Create web adapter (controller) in `presentation/domain/{feature}`
5. Create request/response DTOs in `core/domain/{feature}/dto`
6. Create request/response Object in `presentation/domain/{feature}/data`
7. Add tests for each layer

### Debugging Tests
```bash
# Run with debug output
./gradlew test --debug

# Run single test with more info
./gradlew test --tests "*SomeTest" -i
```

### Updating Dependencies
Dependencies are managed in `build.gradle.kts`. After changes, run:
```bash
./gradlew build --refresh-dependencies
```

## Git Guidelines
- Main branch: `develop`
- Feature branches follow pattern: `feature/{name}` or `fix/{name}`
- Commits in Korean are common in this repo
- Commit Keywords: feat, refac, fix, docs, test
