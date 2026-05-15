---
name: new-rest-endpoint
description: Scaffold a new REST endpoint in this Spring Boot project — creates the JPA entity, repository, service, controller, and a MockMvc test for a feature, following the by-feature package layout used by `book`. Trigger when the user asks to add/scaffold/create a new endpoint, controller, feature, or CRUD resource.
---

# new-rest-endpoint

Scaffold a new REST endpoint following the existing `book` feature's shape.

## Inputs

- **Feature name** (e.g. `Author`). Use PascalCase for the class name and lowercase for the package segment.

## Steps

Given a feature name `Foo`:

1. **Entity** — create `src/main/java/com/learnclaude/library/foo/Foo.java`. JPA `@Entity` with `Long id` (`@GeneratedValue(IDENTITY)`) and at least one `@NotBlank String` field. Mirror the constructors and getters/setters in `Book.java`.

2. **Repository** — create `src/main/java/com/learnclaude/library/foo/FooRepository.java` extending `JpaRepository<Foo, Long>`. No custom queries unless the user asks.

3. **Service** — create `src/main/java/com/learnclaude/library/foo/FooService.java` annotated `@Service`, with constructor injection of the repository, and `findAll()` + `create(Foo)` methods. Mirror `BookService`.

4. **Controller** — create `src/main/java/com/learnclaude/library/foo/FooController.java` annotated `@RestController @RequestMapping("/foos")` (pluralize the path segment). Implement `GET /foos` and `POST /foos` (with `@Valid @RequestBody`, return `201 Created`). Mirror `BookController`.

5. **Test** — create `src/test/java/com/learnclaude/library/foo/FooControllerTest.java`. Mirror `BookControllerTest` exactly: `@SpringBootTest`, `MockMvc`, two tests (`listsFoos`, `createsAFoo`).

6. **Verify** — run `mvn -q test`. If it fails, read the error, fix it, and re-run. Report pass/fail to the user with the test count.

## Conventions to enforce

- Use Java records for any DTOs you introduce. (Reinforces the rule in `CLAUDE.md`.)
- Keep the controller thin: parse → delegate → return. No business logic.
- Tests should not require external services — H2 in-memory DB is already configured.

## Out of scope

Do not generate OpenAPI specs, Liquibase migrations, or DTO mappers. The point is a minimal scaffold the user can extend.
