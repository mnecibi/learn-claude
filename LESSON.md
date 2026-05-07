# Lesson 1 — `CLAUDE.md` (project memory)

## What you'll learn

- Why every project should start with a `CLAUDE.md`
- What belongs in it (and what doesn't)
- How to verify Claude is actually reading it

## Java analogy

Think of `CLAUDE.md` as the file you'd hand a new senior engineer joining the team on day one — a one-pager that fuses `README.md`, `CONTRIBUTING.md`, and your team's style guide. The difference: **Claude reads it on every prompt**, so the cost of writing it down once is repaid forever.

If you've ever onboarded someone and watched them write a method that violated three of your team's unwritten rules, you already understand the value of `CLAUDE.md`.

## The concept

`CLAUDE.md` lives at the repo root. Claude Code automatically loads it into context for every conversation in this directory. It is **not** for documenting *what* the code does (that's javadoc / `README.md`); it is for documenting **how to work in this codebase** in a way that's useful to an LLM teammate:

- The exact commands to build, test, and run
- Layout conventions (package structure, naming)
- Strong rules ("we use Java records for DTOs, never Lombok `@Data`")
- Things that aren't obvious from the code alone (a sharp edge, a workaround, a deprecated path)

Keep it tight. A 2,000-line `CLAUDE.md` is read every turn — bloat costs you tokens, and important rules get lost in the noise.

### Tiny example

```markdown
## Build & test
- `mvn -q test` — run unit tests
- `mvn spring-boot:run` — start the app on :8080

## Conventions
- Use Java records for request/response DTOs.
- Controllers go under `com.learnclaude.library.<feature>`, paired with a `*Service` and `*Repository`.
```

## Your turn (TODO)

Create a `CLAUDE.md` at the repo root that gives Claude enough context to work productively on this codebase. At minimum, include:

- [ ] **Build & test commands** — `mvn` invocations a developer (or Claude) would actually run
- [ ] **Package layout** — describe the `com.learnclaude.library.<feature>` convention
- [ ] **DTO rule** — write a rule that DTOs must be Java records (not classes, not Lombok)
- [ ] **Test conventions** — JUnit 5, Spring Boot test slice, where tests live
- [ ] **One sharp edge** — note that `application.yml` enables H2 console at `/h2-console` for local debugging

Bonus:
- [ ] Mention that Claude should run `mvn -q test` before reporting work as done.
- [ ] Note the Java version (21) so Claude doesn't suggest Java 8 patterns.

## How to verify

After writing `CLAUDE.md`, start a fresh `claude` session in this directory and ask:

> How do I run the tests in this project?

Claude should answer using the exact command from your `CLAUDE.md` — not by grepping `pom.xml` from scratch. If it says "let me check…" and starts running `Read`/`Grep` tools, your `CLAUDE.md` isn't being picked up (check the file is at the repo root, not in a subdirectory).

Then ask:

> Add a DTO for creating a new book.

Claude should produce a Java `record`, not a class with getters/setters. If it produces a class, your DTO rule isn't strong enough — make the wording in `CLAUDE.md` more imperative (`MUST use records`, not `prefer records`).

## Reference

- [Memory & CLAUDE.md](https://docs.claude.com/claude-code/memory) — official docs
- Compare your `CLAUDE.md` to the one on `lesson-01-claude-md-solution`:
  ```bash
  git diff lesson-01-claude-md..lesson-01-claude-md-solution -- CLAUDE.md
  ```
