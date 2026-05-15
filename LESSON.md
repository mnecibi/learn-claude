# Lesson 5 — Subagents (focused workers with their own context)

## What you'll learn

- What a subagent is and how it differs from the main session
- When to delegate to a subagent vs do it yourself
- How to define a project-scoped subagent for Claude Code or Codex

## Java analogy

A subagent is **a worker dispatched to a thread pool**. Same JVM, isolated stack, focused job. The main agent (your conversation) is the dispatcher; subagents are `Callable<String>` tasks: you hand them a brief, they go away and come back with a result.

Two reasons you'd dispatch instead of doing it yourself:

1. **Context isolation** — the main agent's context fills up with code, tool results, and conversation history. A subagent starts fresh: a small brief, no prior conversation. Useful for long, isolated jobs.
2. **Specialization** — you can give the subagent a system prompt that turns it into a focused expert (security reviewer, test writer, code archaeologist). The main agent stays general-purpose.

If you've used Java's `ForkJoinPool` to fan out independent work, you already understand the model.

## The concept

### Where the file lives

| Tool | File | Format |
|---|---|---|
| **Claude Code** | `.claude/agents/<name>.md` | Markdown with YAML frontmatter |
| **Codex** | `.codex/agents/<name>.toml` | TOML |

Both tools support a project scope (committed) and a personal scope (`~/.claude/agents/` or `~/.codex/agents/`). Both let you restrict what the subagent can do.

### Tiny example

**Claude Code (`.claude/agents/spring-security-reviewer.md`):**

```markdown
---
name: spring-security-reviewer
description: Reviews Spring REST controllers for missing authentication, authorization, and input-validation concerns. Use proactively after any controller changes.
tools: Read, Grep, Glob
---

You are a Spring Security expert reviewing controllers for security gaps.
For each `@RestController` you find:
1. Check every endpoint for `@PreAuthorize` or method-level security.
2. Check `@RequestBody` parameters for `@Valid`.
3. Check `@PathVariable`/`@RequestParam` for input sanitization.
4. Report findings as: file:line — severity — issue — suggested fix.
Be terse. Concrete file:line refs only. No general advice.
```

**Codex (`.codex/agents/spring-security-reviewer.toml`):**

```toml
name = "spring-security-reviewer"
description = "Reviews Spring REST controllers for missing authentication, authorization, and input-validation concerns. Use proactively after any controller changes."
sandbox_mode = "read-only"

developer_instructions = """
You are a Spring Security expert reviewing controllers for security gaps.
For each `@RestController` you find:
1. Check every endpoint for `@PreAuthorize` or method-level security.
2. Check `@RequestBody` parameters for `@Valid`.
3. Check `@PathVariable`/`@RequestParam` for input sanitization.
4. Report findings as: file:line — severity — issue — suggested fix.
Be terse. Concrete file:line refs only. No general advice.
"""
```

The body/system-prompt content is the same. The differences:

- **Frontmatter vs top-level TOML.** Claude uses YAML frontmatter; Codex uses TOML key-value at the top of the file.
- **System prompt.** Claude puts it in the markdown body; Codex puts it in a triple-quoted `developer_instructions` string.
- **Tool restriction.** Claude lists allowed tools (`tools: Read, Grep, Glob`); Codex uses `sandbox_mode = "read-only"` (closest analogue — TOML model is sandbox-based, not per-tool).
- **Codex extras.** Codex agents can also set `model`, `model_reasoning_effort`, and `mcp_servers` per agent to override the parent session's defaults.

### Invoking a subagent

| Claude Code | Codex |
|---|---|
| `Task` tool: "use the spring-security-reviewer subagent to audit BookController" | Natural language: "use the spring-security-reviewer agent to audit BookController" |
| Auto-invoked when the main agent matches the `description` field | Auto-invoked via NL match; or use `/agent` to switch/inspect; or use `spawn_agents_on_csv` to fan out |

In both tools, the `description` field is what triggers auto-invocation — write it for the *triggering situation*, not the agent's identity.

### When NOT to use a subagent

- For a 30-second task. Spinning one up costs latency and tokens.
- When you need a back-and-forth conversation. Subagents return one result; they're fire-and-forget.
- When the task needs full project context. Subagents start fresh — they don't see your conversation history.

## Your turn (TODO)

Define two subagents. Use the path/format that matches your tool — or both, so the solution branches the team across tools.

- [ ] **`spring-security-reviewer`** — reviews controllers for missing `@PreAuthorize` / `@Valid` / input validation. Read-only (Claude: `tools: Read, Grep, Glob`; Codex: `sandbox_mode = "read-only"`). Description should make the main agent auto-invoke it after controller edits ("Use proactively after changes to any `@RestController`").

- [ ] **`test-writer`** — writes JUnit 5 + MockMvc tests for a given controller, mirroring the style of `BookControllerTest`. Needs write access (Claude: `tools: Read, Glob, Write`; Codex: `sandbox_mode = "workspace-write"`). Description should trigger when the user asks for tests on a class/feature.

Tips:
- Keep the system prompt **focused**. A subagent that does five things does none of them well. Split into two if you're tempted.
- Spell out the *output format* in the prompt. "Report as: file:line — severity — issue" beats "report findings clearly".
- For the security reviewer, lock down to read-only — reviewers shouldn't ship fixes. That's a separate workflow.

## How to verify

1. **Security reviewer.** In a fresh session:
   ```
   Use the spring-security-reviewer subagent to audit BookController.
   ```
   It should flag: no `@PreAuthorize` anywhere, but `@Valid @RequestBody` is correctly applied. Output should be terse and file:line-anchored.

2. **Auto-invocation.** Ask your assistant to add a new endpoint to `BookController` (e.g. `DELETE /books/{id}`). After the edit, the security reviewer should auto-trigger because of the "use proactively" cue in its description. If it doesn't, your description isn't strong enough — make the trigger more specific.

3. **Test writer.** Ask:
   ```
   Write tests for AuthorController using the test-writer subagent.
   ```
   (You'll need to scaffold `AuthorController` first using the lesson-2 skill.) The subagent should produce a test file structurally identical to `BookControllerTest`.

## Related commands

| Claude Code | Codex | What it does |
|---|---|---|
| `/agents` | `/agent` | Manage / switch / inspect agent configurations. |

See the full command reference for [Claude Code](https://docs.claude.com/claude-code) or [Codex](https://developers.openai.com/codex/cli).

## Reference

- Claude Code subagents: <https://docs.claude.com/claude-code/sub-agents>
- Codex subagents: <https://developers.openai.com/codex/subagents>
- Compare your subagents to the solution:
  ```bash
  git diff lesson-05-subagents..lesson-05-subagents-solution -- .claude/agents/ .codex/agents/
  ```
