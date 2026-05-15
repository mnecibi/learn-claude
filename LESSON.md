# Lesson 6 — Capstone: ship the `Author` feature using everything

## What you'll do

Add a new `Author` REST feature to the library service end-to-end, using **only** the customizations you set up in lessons 1–5. No manual scaffolding, no copy-paste from `book/`. Let your project-memory file, your skill, your hooks, your MCP server, and your subagents do the work.

This is the lesson where the pieces stop feeling like separate tricks and start feeling like a workflow. It works the same whether you're driving with Claude Code or Codex — the customizations you've built map to either tool.

## The challenge

Your `Author` feature should support:

- `GET /authors` → list all authors
- `POST /authors` → create an author (fields: `name`, `bio`)
- An `AuthorControllerTest` covering both with the project's existing style

It must satisfy these constraints, in order:

- [ ] **Use the lesson-2 skill.** Invoke `/new-rest-endpoint Author` (Claude) or `$new-rest-endpoint Author` (Codex), or trigger it via natural language. The skill should produce all five files. Do **not** open the `book/` package and copy from it yourself.
- [ ] **Let the lesson-3 hooks fire.** Watch `mvn -q compile` run after each Java edit. Watch `mvn -q test` run when the assistant wraps up. If the hooks don't fire, your hooks config from lesson 3 is missing or broken (or, for Codex, you haven't run `/hooks` to trust the project).
- [ ] **Honor the project-memory file.** Any DTO the assistant creates must be a Java record. Spot-check the diff: `git diff main..HEAD` should show no `class FooDto { ... }` with getters/setters.
- [ ] **Have the lesson-5 subagent review the result.** Invoke `spring-security-reviewer` against `AuthorController`. Read the findings — they should match what you'd flag yourself for an unauthenticated controller.
- [ ] **Use the MCP server.** Ask the assistant to "list every `@RestController` in the project" before and after. Both calls should show the MCP filesystem server being invoked rather than `Grep` over the whole repo.

## A suggested session

```
You:        Add an Author feature — name, bio. Same shape as Book.
Assistant:  [auto-invokes the new-rest-endpoint skill]
            [creates 5 files; PostToolUse hook compiles after each]
            [Stop hook runs full test suite — green]
            [auto-invokes spring-security-reviewer subagent on AuthorController]
            [returns: file:line — high — POST /authors has no @PreAuthorize — fix: add method-level security]
You:        Auth is out of scope for this exercise — leave the finding for follow-up.
```

If your session looks like that, you've internalized lessons 1–5.

## When you're done

- [ ] `mvn -q test` — at least 4 tests, all green (Book × 2 + Author × 2 minimum).
- [ ] `git diff main..HEAD` shows the new `author/` package and its test.
- [ ] You did not author any of the `Author` source files by hand.

## Related commands

| Claude Code | Codex | What it does |
|---|---|---|
| `/skills` | `/skills` | List available skills (lesson 2). |
| `/hooks` | `/hooks` | View / trust hook configurations (lesson 3). |
| `/mcp` | `/mcp` | Manage MCP server connections (lesson 4). |
| `/agents` | `/agent` | Manage agent configurations (lesson 5). |
| `/review` | `/review` | Review a pull request locally. Natural next step after the security reviewer flags issues. |

See the full command reference for [Claude Code](https://docs.claude.com/claude-code) or [Codex](https://developers.openai.com/codex/cli).

## Reference

The solution branch shows what the resulting tree looks like:

```bash
git diff lesson-06-capstone..lesson-06-capstone-solution
```

Don't peek until you've tried the workflow yourself — the muscle memory is the point.
