# Lesson 5 — Subagents (focused workers with their own context)

## What you'll learn

- What a subagent is and how it differs from the main Claude session
- When to delegate to a subagent vs do it yourself
- How to define a project-scoped subagent in `.claude/agents/`

## Java analogy

A subagent is **a worker dispatched to a thread pool**. Same JVM, isolated stack, focused job. The main agent (your conversation) is the dispatcher; subagents are `Callable<String>` tasks: you hand them a brief, they go away and come back with a result.

Two reasons you'd dispatch instead of doing it yourself:

1. **Context isolation** — the main agent's context fills up with code, tool results, and conversation history. A subagent starts fresh: 8k tokens of brief, no prior conversation. Useful for long, isolated jobs.
2. **Specialization** — you can give the subagent a system prompt that turns it into a focused expert (security reviewer, test writer, code archaeologist). The main agent stays general-purpose.

If you've used Java's `ForkJoinPool` to fan out independent work, you already understand the model.

## The concept

A subagent is defined as a markdown file under `.claude/agents/<name>.md`:

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

The `tools` line restricts what the subagent can do — security reviewers don't need `Edit` or `Bash`.

The main agent invokes a subagent via the `Task` tool. You can call it explicitly ("review BookController with the spring-security-reviewer subagent") or trust the main agent to choose based on the `description` field.

### When NOT to use a subagent

- For a 30-second task. Spinning up a subagent costs latency and tokens.
- When you need a back-and-forth conversation. Subagents return one message; they're fire-and-forget.
- When the task needs full project context. Subagents start fresh — they don't see your conversation history.

## Your turn (TODO)

Define two subagents.

- [ ] **`.claude/agents/spring-security-reviewer.md`** — reviews controllers for missing `@PreAuthorize` / `@Valid` / input validation. Tools: `Read, Grep, Glob` (read-only). Description should make the main agent auto-invoke it after controller edits ("Use proactively after changes to any `@RestController`").

- [ ] **`.claude/agents/test-writer.md`** — writes JUnit 5 + MockMvc tests for a given controller, mirroring the style of `BookControllerTest`. Tools: `Read, Glob, Write` (needs `Write` because it's producing test files). Description should trigger when the user asks for tests on a class/feature.

Tips:
- Keep the system prompt **focused**. A subagent that does five things does none of them well. If you're tempted to add a fifth bullet, split it into a second subagent.
- Spell out the *output format* in the prompt. "Report as: file:line — severity — issue" beats "report findings clearly".
- For the security reviewer, explicitly omit `Edit`/`Write` from `tools` — reviewers shouldn't ship fixes. That's a separate workflow.

## How to verify

1. **Security reviewer.** In a fresh `claude` session, run:
   ```
   Use the spring-security-reviewer subagent to audit BookController.
   ```
   It should flag: no `@PreAuthorize` anywhere, but `@Valid @RequestBody` is correctly applied. Output should be terse and file:line-anchored.

2. **Auto-invocation.** Ask Claude to add a new endpoint to `BookController` (e.g. `DELETE /books/{id}`). After the edit, the security reviewer should auto-trigger because of the "use proactively" cue in its description. If it doesn't, your description isn't strong enough — make the trigger more specific.

3. **Test writer.** Ask:
   ```
   Write tests for AuthorController using the test-writer subagent.
   ```
   (You'll need to scaffold `AuthorController` first using the lesson-2 skill.) The subagent should produce a test file structurally identical to `BookControllerTest`.

## Reference

- [Subagents documentation](https://docs.claude.com/claude-code/sub-agents)
- Compare your subagents to the solution:
  ```bash
  git diff lesson-05-subagents..lesson-05-subagents-solution -- .claude/agents/
  ```
