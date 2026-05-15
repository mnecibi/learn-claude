# Lesson 2 — Skills (reusable workflows)

## What you'll learn

- What a skill is and when to write one
- How to package a multi-step recipe so you can invoke it with a short command
- How to make skills auto-trigger from natural-language prompts (no slash needed)

## Java analogy

A skill is like a `@Service` bean — write it once, invoke it wherever you need it. Without skills, you re-explain the same recipe to your assistant every time ("scaffold a controller, then a service, then a JPA repo, then a test"). With a skill, you trigger it by name and the recipe runs.

If you've ever written a shell script to wrap a 5-step copy-paste workflow, you understand the value: the work is one-shot, but you do it twice a week.

## The concept

A skill is a folder containing a `SKILL.md` file. The frontmatter holds `name` and `description`; the body is the recipe (numbered steps the assistant follows).

### Where the file lives

| Tool | Path | Invocation |
|---|---|---|
| **Claude Code** | `.claude/skills/<name>/SKILL.md` | `/<name>` in-session, or auto-triggered by the `description` |
| **Codex** | `.agents/skills/<name>/SKILL.md` (note: under `.agents/`, **not** `.codex/`) | `$<name>` in-session, or `/skills` to browse, or auto-triggered by the `description` |

Both tools also support a personal scope (`~/.claude/skills/` and `~/.agents/skills/`) — handy for "my own" skills that travel across projects.

> **Codex note:** the older `/prompts:<name>` mechanism (loaded from `~/.codex/prompts/*.md`) is **deprecated**. Use Skills for new work.

### Tiny example

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

The body is **identical** between Claude Code and Codex — only the path differs. If you use both tools, ship the same `SKILL.md` at both paths.

**Two ways to invoke:**

1. **Explicit:** type `/new-rest-endpoint Author` (Claude) or `$new-rest-endpoint Author` (Codex). Your assistant runs the recipe.
2. **Auto-trigger:** type "add a new author endpoint" — your assistant reads the skill's `description` field and decides to use it. The `description` is the most important line in the file: it's what the assistant pattern-matches against your natural-language request.

### Why not just put this in the project-memory file?

Because the project-memory file is loaded **every** turn (token cost). A skill is loaded **only when invoked**. Use the memory file for always-on conventions; use skills for occasional multi-step recipes.

## Your turn (TODO)

Author the `new-rest-endpoint` skill so you can scaffold a feature in one command.

- [ ] Create the skill file at `.claude/skills/new-rest-endpoint/SKILL.md` (Claude) or `.agents/skills/new-rest-endpoint/SKILL.md` (Codex) — or both, with identical content.
- [ ] Frontmatter: `name`, `description`. Write a `description` that makes your assistant auto-trigger the skill when the user says things like "add a new endpoint for X" or "scaffold a foo controller". One sentence. Be specific about what the skill does.
- [ ] In the body, list the exact files to create — referencing the `book` package as the template to copy. Include filenames, package declarations, and a note to mirror `BookControllerTest` for the test file.
- [ ] Add a final step: run `mvn -q test` and report failures.
- [ ] (Optional) Add a `list-rest-endpoints` skill that greps for `@RestController` and lists all REST surface in the project.

## How to verify

Two checks (run them in a fresh session — `claude` or `codex` — in this directory).

**Explicit invocation.**

```
/new-rest-endpoint Author        # Claude
$new-rest-endpoint Author        # Codex
```

The assistant should create all 5 files in the right packages, with code that compiles. Run `mvn -q test` and confirm the new `AuthorControllerTest` passes.

**Auto-trigger.** Discard the changes (`git checkout .`) and try natural language:

```
Add a Publisher REST endpoint following the same pattern as Book.
```

The assistant should choose to invoke the skill on its own. If it doesn't — if it instead writes the files manually from scratch — your `description` isn't doing its job. Make the description more precise about what triggers it.

## Related commands

| Claude Code | Codex | What it does |
|---|---|---|
| `/skills` | `/skills` | List available skills. Confirms your `new-rest-endpoint` skill is discovered. |
| `/plugin` | `/skill-installer` | Manage installable skill packages from outside the repo. |

See the full command reference for [Claude Code](https://docs.claude.com/claude-code) or [Codex](https://developers.openai.com/codex/cli).

## Reference

- Claude Code skills: <https://docs.claude.com/claude-code/skills>
- Codex skills: <https://developers.openai.com/codex/skills>
- Compare your skill(s) to the solution branch:
  ```bash
  git diff lesson-02-skills..lesson-02-skills-solution -- .claude/ .agents/
  ```
