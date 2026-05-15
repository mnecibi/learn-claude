# learn-ai-coding — project notes for your AI assistant

A small Spring Boot service used as a teaching vehicle for the AI-Assisted Coding for Java Developers course. Treat this file as the source of truth for how to work in this repo.

This file is read by Codex CLI at session start. A mirror copy of the same content lives at `CLAUDE.md` for Claude Code. Keep the two files in sync.

## Stack

- Java 21
- Spring Boot 3.3.x (web, data-jpa, validation)
- H2 in-memory database (no external DB needed)
- JUnit 5 + Spring Boot test

## Build & test

- `mvn -q test` — run unit tests. **Run this before reporting work as done.**
- `mvn -q compile` — compile only.
- `mvn spring-boot:run` — start the app on `http://localhost:8080`.
- `mvn -q package` — build the executable jar.

## Package layout

Code is organized **by feature**, not by layer:

```
com.learnclaude.library.<feature>/
    <Feature>.java            — JPA entity
    <Feature>Repository.java  — Spring Data repository
    <Feature>Service.java     — business logic
    <Feature>Controller.java  — REST endpoints
```

The current feature is `book`. New features (e.g. `author`) follow the same shape.

## Conventions

- **DTOs MUST be Java records.** Never use classes with getters/setters or Lombok `@Data` for request/response payloads.
- Validation uses `jakarta.validation` annotations (`@NotBlank`, `@Valid`) — already on the classpath.
- Repositories extend `JpaRepository<Entity, Long>`. Don't write custom `@Query` until you actually need one.
- Controllers stay thin: parse, delegate to service, return. No business logic in controllers.

## Test conventions

- Tests live under `src/test/java/com/learnclaude/library/<feature>/`.
- Use `@SpringBootTest` with `MockMvc` for controller tests (see `BookControllerTest`).
- Test methods read like sentences: `listsBooks`, `createsABook`, `rejectsInvalidPayload`.

## Sharp edges

- The H2 console is enabled at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:library`, user `sa`, blank password). Useful for debugging during a `mvn spring-boot:run` session, but **never enable it in a real prod app**.
- `spring.jpa.open-in-view` is on by default — Spring's choice, not ours. If you add lazy associations, be aware queries may fire during view rendering.
