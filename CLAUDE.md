# CLAUDE.md

Guidance for working with the DCD codebase.

## Project Overview

DCD is a Spring Boot REST API (Kotlin, Java 17) managing Docker-based applications. Supports multiple frameworks (Spring Boot, Django, etc.), Docker container orchestration, and application lifecycle management. Infrastructure: MariaDB, Redis (caching/rate limiting), Docker Java client.

## Architecture: Hexagonal + Ports & Adapters

```
src/main/kotlin/com/dcd/server/
├── core/domain/              # Business logic & use cases
│   ├── {feature}/usecase/    # *UseCase interfaces
│   ├── {feature}/spi/        # Ports (QueryPort, CommandPort)
│   ├── {feature}/dto/        # Data transfer objects
│   ├── {feature}/exception/  # Custom domain exceptions
│   └── {feature}/model/      # Domain models
├── persistence/              # SPI implementations (adapters)
│   └── {feature}/\*PersistenceAdapter.kt
├── presentation/             # HTTP adapters (REST controllers)
│   └── {feature}/\*WebAdapter.kt
└── infrastructure/           # Spring config, third-party clients
    ├── global/config/        # Security, WebSocket, Caching
    ├── global/security/      # JWT, authentication
    └── global/thirdparty/    # Docker, Git, Mail clients
```

**Dependency Flow**: 
```
Presentation (Request) → .toDto() → ReqDto
                ↓
            UseCase (domain logic)
                ↓
            ResDto → .toResponse() → Response (Presentation)
```

**Ports & Adapters Flow**:
```
UseCase → Port (interface) ← Adapter (implementation)
                           ├── *PersistenceAdapter (persistence layer)
                           └── *Adapter (infrastructure layer)
```

**Each Feature Has**:
- `*UseCase` interface + implementation in `core.domain.{feature}.usecase`
- `*Port` interfaces (Query/Command) in `core.domain.{feature}.spi`
- `*Request`/`*Response` objects in `presentation.{feature}.data.request/response` (API contracts)
- `*ReqDto`/`*ResDto` in `core.domain.{feature}.dto` (UseCase contracts)
- `.toDto()` extension to convert Request → ReqDto
- `.toResponse()` extension to convert ResDto → Response
- `*PersistenceAdapter` in `persistence.{feature}` implementing ports
- `*Adapter` in `infrastructure.{feature}` or `infrastructure.global` implementing ports
- `*WebAdapter` (REST controller) in `presentation.{feature}`

## Development Quick Start

**Build & Test**:
```bash
./gradlew clean build              # Full build + tests
./gradlew build -x test            # Build without tests
./gradlew test --tests "*ApplicationWebAdapterTest"
./gradlew test --tests "*ApplicationWebAdapterTest.createApplication*"
```

**Run Application** (port 8081):
```bash
# One-time setup
docker network create dcd

# Start infrastructure + app
docker-compose up -d               # MariaDB, Redis, Nginx
./gradlew bootRun
```

**Code Quality**: Kotlin 1.8.22 + strict JSR305 annotations enforced by compiler.

## Configuration

**Environment Variables** (required):
- **Database**: `DB_URL`, `DB_USER`, `DB_PASSWORD`, `DB_DRIVER`, `DB_HIBERNATE_DIALECT`
- **Redis**: `REDIS_HOST`, `REDIS_PORT`, `REDIS_USERNAME`, `REDIS_PASSWORD`
- **JWT**: `JWT_ACCESS_SECRET`, `JWT_REFRESH_SECRET`, `JWT_ACCESS_TIME`, `JWT_REFRESH_TIME`
- **Security**: `AES_SECRET`, `AES_INIT_VECTOR`
- **Mail**: `MAIL_ADDRESS`, `MAIL_PASSWORD`
- **Docker**: `DOCKER_REGISTRY_URL`, `DOCKER_TOKEN`
- **Paths**: `DOMAIN_CONFIG_PATH`

**Files**: `src/main/resources/application.yml` (main), `application-test.yml` (test profile), `docker-compose.yml` (infrastructure).

## Testing: Kotest + MockK

**Style**: BehaviorSpec with given/when/then. **Location**: `src/test/kotlin/` mirrors source structure.

```kotlin
class ApplicationWebAdapterTest : BehaviorSpec({
    val mockUseCase = mockk<SomeUseCase>()
    val adapter = WebAdapter(mockUseCase)

    given("Setup") {
        `when`("Action") {
            every { mockUseCase.execute(any()) } returns expectedResult
            val result = adapter.someMethod()
            then("Expectation") {
                result shouldBe expected
                verify { mockUseCase.execute(any()) }
            }
        }
    }
})
```

**Key**: Mock only UseCases (in *WebAdapterTest). Integration tests hit real DB via *PersistenceAdapterTest.

## Key Patterns

### 1. Ports & Adapters (SPI)
Domain defines interfaces, infrastructure implements:
- `*Port` (query/command operations) in `core.domain.{feature}.spi`
- `*PersistenceAdapter` implements in `persistence`
- Enables testing with mocks, swapping implementations

### 2. AOP for Cross-Cutting Concerns
Annotations handle authorization, rate limiting, validation:
- `@Limit` → `LimitAspect` (rate limiting via bucket4j)
- `@WorkspaceOwnerVerification` → `OwnerValidateAspect`
- `@CheckEmailCertificate` → `EmailCertificateAspect`
- `@Lock` → distributed locking

Use for validation logic that doesn't belong in UseCases.

### 3. Domain Exceptions
Extend `BasicException` in `core.domain.{feature}.exception`:
- Application layer catches and translates to HTTP responses
- Clear error propagation path

### 4. Event-Driven Actions
Application lifecycle events (e.g., `DeployApplicationEvent`) trigger side effects. Publish from UseCases, handle in listeners.

### 5. DTO & Data Mapping
**Presentation Layer Flow**:
- **Request**: Client → `*Request` object → `.toDto()` → `*ReqDto` → UseCase
- **Response**: UseCase → `*ResDto` → `.toResponse()` → `*Response` object → Client

**Conversion Layer**:
- `*Request` (API input contract) in `presentation.{feature}.data.request`
- `*Response` (API output contract) in `presentation.{feature}.data.response`
- `*ReqDto` (UseCase input) in `core.domain.{feature}.dto`
- `*ResDto` (UseCase output) in `core.domain.{feature}.dto`

**Rules**:
- Never serialize entities directly; always use Request/Response
- UseCase always works with ReqDto/ResDto, never Request/Response
- Extensions `.toDto()` and `.toResponse()` handle conversions

## Infrastructure Components

Ports can be implemented in infrastructure layer for cross-cutting concerns:

| Feature | Library | Port Implementation | Location |
|---------|---------|-------------------|----------|
| **Authentication** | Spring Security + JWT (JJWT) | `GenerateTokenPort`, `ParseTokenPort` → `GenerateTokenAdapter`, `ParseTokenAdapter` | `infrastructure.global.jwt.adapter` |
| **Security** | Spring Security | `SecurityPort` → `SecurityAdapter` | `infrastructure.global.security` |
| **Encryption** | AES | `EncryptPort` → `EncryptAdapter` | `infrastructure.global.security` |
| **Database** | Spring Data JPA + Hibernate, MariaDB | `*Port` → `*PersistenceAdapter` | `persistence.{feature}` |
| **Caching** | Spring Cache + Redis (Redisson) | Configured in `infrastructure.global.config` | `infrastructure.global.config` |
| **Rate Limiting** | bucket4j + Redis | `LimitPort` → `RedisLimitAdapter`, `LocalLimitAdapter` | `infrastructure.global.thirdparty.bucket4j` |
| **WebSocket** | Spring WebSocket | WebSocket message handling | `infrastructure.global.config.WebSocketConfig` |
| **Container Management** | docker-java | `*Adapter` for Docker operations | `infrastructure.domain.application.adapter` |
| **Version Control** | JGit | `*Adapter` for Git operations | `infrastructure.domain.application.adapter` |
| **Locking** | Redisson | `@Lock` AOP | `infrastructure.global.aop` |
| **Token Blacklist** | Redis | `TokenBlackListPort` | `infrastructure.global` |

## Adding a New Feature

Follow this complete structure:

1. **Domain** (`core/domain/{feature}/`)
   - `usecase/*UseCase.kt` (interface + impl)
   - `spi/*Port.kt` (Query/Command interfaces)
   - `dto/*ReqDto.kt`, `*ResDto.kt`
   - `exception/*Exception.kt` (extend BasicException)
   - `model/` (domain models)

2. **Presentation** (`presentation/{feature}/`)
   - `data/request/*Request.kt` (API input contracts)
   - `data/response/*Response.kt` (API output contracts)
   - `data/extension/*Extension.kt` (.toDto(), .toResponse() converters)
   - `*WebAdapter.kt` (REST controller)
   - Call: Request → .toDto() → UseCase → .toResponse() → Response

3. **Persistence** (`persistence/{feature}/`)
   - `domain/*Entity.kt` (JPA entity)
   - `*Repository.kt` (JPA repository)
   - `*PersistenceAdapter.kt` (implements Port)

4. **Infrastructure** (conditional, if needed)
   - `infrastructure/{feature}/adapter/*Adapter.kt` (implements Port)
   - Or `infrastructure/global/adapter/*Adapter.kt` (cross-cutting)
   - Examples: Token generation, encryption, external APIs, caching

5. **Tests** (mirror structure in `src/test/kotlin/`)
   - `*WebAdapterTest` (mock UseCase)
   - `*PersistenceAdapterTest` (real DB via H2/test profile)

**Ports Can Be Implemented In**:
- `persistence.{feature}.*PersistenceAdapter` (database operations)
- `infrastructure.{feature}.adapter.*Adapter` (feature-specific external services)
- `infrastructure.global.adapter.*Adapter` (shared services: JWT, encryption, rate limiting)
