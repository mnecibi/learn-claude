# Lesson 1 — Project memory

## What you'll learn

- Why every project should start with a project-memory file your assistant reads on every turn
- What belongs in it (and what doesn't)
- How to verify your assistant is actually reading it

## Java analogy

Think of the project-memory file as the page you'd hand a new senior engineer joining the team on day one — a one-pager that fuses `README.md`, `CONTRIBUTING.md`, and your team's style guide. The difference: **your AI assistant reads it on every prompt**, so the cost of writing it down once is repaid forever.

If you've ever onboarded someone and watched them write a method that violated three of your team's unwritten rules, you already understand the value of this file.

## The concept

The project-memory file lives at the repo root. Your AI assistant automatically loads it into context for every conversation in this directory. It is **not** for documenting *what* the code does (that's javadoc / `README.md`); it is for documenting **how to work in this codebase** in a way that's useful to an LLM teammate:

- The exact commands to build, test, and run
- Layout conventions (package structure, naming)
- Strong rules ("we use Java records for DTOs, never Lombok `@Data`")
- Things that aren't obvious from the code alone (a sharp edge, a workaround, a deprecated path)

Keep it tight. A 2,000-line memory file is read every turn — bloat costs you tokens, and important rules get lost in the noise.

### Where the file lives

| Tool | File | Notes |
|---|---|---|
| **Claude Code** | `CLAUDE.md` at repo root | Auto-loaded each turn. Imports via `@path/to/other.md` are supported. |
| **Codex** | `AGENTS.md` at repo root | Auto-loaded each turn. Codex also walks nested directories (`<subdir>/AGENTS.md`) and supports `AGENTS.override.md` for layered overrides. |

The body content is the same plain Markdown in both files. If you use both tools, ship both files with identical content (or have one be a one-line pointer to the other).

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

Create a project-memory file at the repo root (`CLAUDE.md` if you're using Claude Code, `AGENTS.md` if you're using Codex — or both, with the same content) that gives your assistant enough context to work productively on this codebase. At minimum, include:

- [ ] **Build & test commands** — `mvn` invocations a developer (or your assistant) would actually run
- [ ] **Package layout** — describe the `com.learnclaude.library.<feature>` convention
- [ ] **DTO rule** — write a rule that DTOs must be Java records (not classes, not Lombok)
- [ ] **Test conventions** — JUnit 5, Spring Boot test slice, where tests live
- [ ] **One sharp edge** — note that `application.yml` enables H2 console at `/h2-console` for local debugging

Bonus:
- [ ] Mention that the assistant should run `mvn -q test` before reporting work as done.
- [ ] Note the Java version (21) so the assistant doesn't suggest Java 8 patterns.

## How to verify

Start a fresh session in this directory (`claude` or `codex`) and ask:

> How do I run the tests in this project?

The assistant should answer using the exact command from your memory file — not by grepping `pom.xml` from scratch. If it says "let me check…" and starts running file-reading tools, your memory file isn't being picked up (check the file is at the repo root and named exactly `CLAUDE.md` or `AGENTS.md`).

Then ask:

> Add a DTO for creating a new book.

The assistant should produce a Java `record`, not a class with getters/setters. If it produces a class, your DTO rule isn't strong enough — make the wording more imperative (`MUST use records`, not `prefer records`).

## Related commands

| Claude Code | Codex | What it does |
|---|---|---|
| `/init` | `codex init` | Initialize a project with a memory file. |
| `/memory` | (edit `AGENTS.md` directly) | Edit/inspect the memory file from inside the session. |

See the full command reference for [Claude Code](https://docs.claude.com/claude-code) or [Codex](https://developers.openai.com/codex/cli).

## Reference

- Claude Code memory: <https://docs.claude.com/claude-code/memory>
- Codex `AGENTS.md` guide: <https://developers.openai.com/codex/guides/agents-md>
- Compare your file(s) to the reference implementation:
  ```bash
  git diff lesson-01-project-memory..lesson-01-project-memory-solution
  ```
