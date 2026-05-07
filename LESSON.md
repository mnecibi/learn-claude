# Lesson 6 — Capstone: ship the `Author` feature using everything

## What you'll do

Add a new `Author` REST feature to the library service end-to-end, using **only** the Claude Code tooling you set up in lessons 1–5. No manual scaffolding, no copy-paste from `book/`. Let your `CLAUDE.md`, your skill, your hooks, your MCP server, and your subagents do the work.

This is the lesson where the pieces stop feeling like separate tricks and start feeling like a workflow.

## The challenge

Your `Author` feature should support:

- `GET /authors` → list all authors
- `POST /authors` → create an author (fields: `name`, `bio`)
- An `AuthorControllerTest` covering both with the project's existing style

It must satisfy these constraints, in order:

- [ ] **Use the lesson-2 skill.** Invoke `/new-rest-endpoint Author` (or trigger it via natural language). The skill should produce all five files. Do **not** open the `book/` package and copy from it yourself.
- [ ] **Let the lesson-3 hooks fire.** Watch `mvn -q compile` run after each Java edit. Watch `mvn -q test` run when Claude wraps up. If the hooks don't fire, your `.claude/settings.json` from lesson 3 is missing or broken.
- [ ] **Honor `CLAUDE.md`.** Any DTO Claude creates must be a Java record. Spot-check the diff: `git diff main..HEAD` should show no `class FooDto { ... }` with getters/setters.
- [ ] **Have the lesson-5 subagent review the result.** Invoke `spring-security-reviewer` against `AuthorController`. Read the findings — they should match what you'd flag yourself for an unauthenticated controller.
- [ ] **Use the MCP server.** Ask Claude to "list every `@RestController` in the project" before and after. Both calls should show the MCP filesystem server being invoked rather than `Grep` over the whole repo.

## A suggested session

```
You:    Add an Author feature — name, bio. Same shape as Book.
Claude: [auto-invokes /new-rest-endpoint Author skill]
        [creates 5 files; PostToolUse hook compiles after each]
        [Stop hook runs full test suite — green]
        [auto-invokes spring-security-reviewer subagent on AuthorController]
        [returns: file:line — high — POST /authors has no @PreAuthorize — fix: add method-level security]
You:    Auth is out of scope for this exercise — leave the finding for follow-up.
```

If your session looks like that, you've internalized lessons 1–5.

## When you're done

- [ ] `mvn -q test` — at least 4 tests, all green (Book × 2 + Author × 2 minimum).
- [ ] `git diff main..HEAD` shows the new `author/` package and its test.
- [ ] You did not author any of the `Author` source files by hand.

## Related commands

| Command | What it does |
|---|---|
| `/skills` | List available skills (introduced in lesson 2). The capstone exercises your `new-rest-endpoint` skill. |
| `/hooks` | View hook configurations (introduced in lesson 3). The capstone relies on the compile-on-edit, test-on-stop, and pom guard hooks. |
| `/mcp` | Manage MCP server connections (introduced in lesson 4). The capstone uses the filesystem MCP server. |
| `/agents` | Manage agent configurations (introduced in lesson 5). The capstone calls the `spring-security-reviewer` and `test-writer` subagents. |
| `/review` | Review a pull request locally. Natural next step after the `spring-security-reviewer` subagent flags issues. |

See [the full command reference](https://code.claude.com/docs/en/commands) for everything else.

## Reference

The solution branch shows what the resulting tree looks like:

```bash
git diff lesson-06-capstone..lesson-06-capstone-solution
```

Don't peek until you've tried the workflow yourself — the muscle memory is the point.
