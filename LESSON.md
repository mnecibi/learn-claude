# Lesson 2 — Skills (reusable workflows)

## What you'll learn

- What a Claude Code skill is and when to write one
- How to package a multi-step recipe so you can invoke it as `/skill-name`
- How to make skills auto-trigger from natural-language prompts (no slash needed)

## Java analogy

A skill is like a `@Service` bean — write it once, inject it (invoke it) wherever you need it. Without skills, you re-explain the same recipe to Claude every time ("scaffold a controller, then a service, then a JPA repo, then a test"). With a skill, you say `/new-rest-endpoint Author` and the recipe runs.

If you've ever written a shell script to wrap a 5-step copy-paste workflow, you understand the value: the work is one-shot, but you do it twice a week.

## The concept

A skill is just a folder under `.claude/skills/<name>/` containing a `SKILL.md`. The frontmatter looks like:

```markdown
---
name: new-rest-endpoint
description: Scaffold a new REST endpoint (controller + service + repository + test) following the project's by-feature package layout.
---

# new-rest-endpoint

Given a feature name like `Author`:

1. Create `com.learnclaude.library.author/Author.java` (JPA entity, copy the shape of `Book.java`).
2. Create `AuthorRepository extends JpaRepository<Author, Long>`.
3. Create `AuthorService` with `findAll()` and `create(...)`.
4. Create `AuthorController` with `GET /authors` and `POST /authors`.
5. Create `AuthorControllerTest` mirroring `BookControllerTest`.
6. Run `mvn -q test` to confirm.
```

**Two ways to invoke:**

1. **Explicit slash**: type `/new-rest-endpoint Author` — Claude runs the recipe.
2. **Auto-trigger**: type "add a new author endpoint" — Claude reads the skill's `description` field and decides to use it. The `description` is the most important line in the file: it's what Claude pattern-matches against your natural-language request.

Skills can scope to a project (`.claude/skills/`) or a user (`~/.claude/skills/`). Project-scoped skills travel with the repo — the whole team gets them.

### Why not just put this in `CLAUDE.md`?

Because `CLAUDE.md` is loaded **every** turn (token cost). A skill is loaded **only when invoked**. Use `CLAUDE.md` for always-on conventions; use skills for occasional multi-step recipes.

## Your turn (TODO)

Author the `new-rest-endpoint` skill so you can scaffold a feature in one command.

- [ ] Create `.claude/skills/new-rest-endpoint/SKILL.md` with frontmatter (`name`, `description`).
- [ ] Write a `description` that makes Claude auto-trigger the skill when the user says things like "add a new endpoint for X" or "scaffold a foo controller". One sentence. Be specific about what the skill does.
- [ ] In the body, list the exact files to create — referencing the `book` package as the template to copy. Include filenames, package declarations, and a note to mirror `BookControllerTest` for the test file.
- [ ] Add a final step: run `mvn -q test` and report failures.
- [ ] (Optional) Add a `/list-rest-endpoints` skill that greps for `@RestController` and lists all REST surface in the project.

## How to verify

Two checks:

**Explicit invocation.** In a fresh `claude` session in this directory:

```
/new-rest-endpoint Author
```

Claude should create all 5 files in the right packages, with code that compiles. Run `mvn -q test` and confirm the new `AuthorControllerTest` passes.

**Auto-trigger.** Discard the changes (`git checkout .`) and try natural language:

```
Add a Publisher REST endpoint following the same pattern as Book.
```

Claude should choose to invoke `/new-rest-endpoint` on its own. If it doesn't — if it instead writes the files manually from scratch — your `description` isn't doing its job. Make the description more precise about what triggers it.

## Related commands

| Command | What it does |
|---|---|
| `/skills` | List available skills; press `t` to sort by token cost. Confirms your `new-rest-endpoint` skill is discovered. |
| `/plugin` | Manage plugins, the distribution channel for skills beyond `.claude/skills/`. |

See [the full command reference](https://code.claude.com/docs/en/commands) for everything else.

## Reference

- [Skills documentation](https://docs.claude.com/claude-code/skills)
- Compare your skill to the solution branch:
  ```bash
  git diff lesson-02-skills..lesson-02-skills-solution -- .claude/
  ```
