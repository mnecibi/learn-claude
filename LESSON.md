# Lesson 3 — Hooks (automate around tool calls)

## What you'll learn

- What Claude Code hooks are and which events they fire on
- How to use hooks to enforce policies and automate verification
- The difference between blocking and non-blocking hooks

## Java analogy

Hooks are **AOP for your AI assistant**. Think Spring `@Around` advice or a servlet `Filter`: a piece of code that fires *around* an event you don't own — in this case, Claude's tool calls — letting you observe, modify, or veto.

You wouldn't dream of running a Spring app without filters for auth/logging/metrics. Once you've added hooks to a Claude Code project, you'll feel the same way: "how did I work without auto-test-on-stop?"

## The concept

Hooks are configured in `.claude/settings.json`. The interesting events:

| Event | Fires when | Typical use |
|---|---|---|
| `PreToolUse` | Before a tool runs | Block/confirm risky operations |
| `PostToolUse` | After a tool succeeds | Auto-format, recompile, log |
| `Stop` | When Claude finishes its turn | Run tests, update CI status |
| `UserPromptSubmit` | When you submit a prompt | Inject context, gate sensitive prompts |

A hook is just a shell command. Its **exit code matters**:

- `0` → silent success
- `2` → blocking error; the message on stderr is shown to Claude (PreToolUse) or to the user (PostToolUse)
- other → non-blocking warning, message on stderr is shown to the user

So a `PreToolUse` hook that exits 2 prevents the tool call from running. A `PostToolUse` hook that exits 2 doesn't undo the tool call (impossible) but flags the issue to Claude so it can react.

### Tiny example

```json
{
  "hooks": {
    "PostToolUse": [
      {
        "matcher": "Edit|Write",
        "hooks": [
          { "type": "command", "command": "mvn -q compile" }
        ]
      }
    ]
  }
}
```

After every `Edit` or `Write`, Maven recompiles. If compile fails, exit code is non-zero and Claude sees the error — it'll usually fix it on its own without you saying a word.

## Your turn (TODO)

Add a `.claude/settings.json` with three hooks. Each hook teaches a different pattern.

- [ ] **PostToolUse — auto-compile.** Match `Edit|Write|MultiEdit`. Run `mvn -q compile -pl . --quiet` (or just `mvn -q compile`). The point: catch breakage immediately, before Claude moves on.

- [ ] **Stop — auto-test.** Match the `Stop` event. Run `mvn -q test`. The point: every turn ends with a green test suite or a clear failure.

- [ ] **PreToolUse — pom.xml guard.** Match `Edit|Write` *and* match the file path `pom.xml`. Echo a warning to stderr and exit `2` to block the edit. The point: enforce that pom changes get human review (you don't want Claude bumping Spring Boot 3.3 → 4.0 without you noticing).

Hints:
- The `matcher` field is a regex over tool names. To match a path, your hook command itself reads the input from stdin (JSON containing the tool's args) — for the pom guard, write a small inline script: read stdin, check if `file_path` ends in `pom.xml`, exit accordingly.
- Keep hooks **fast**. A 30-second hook makes Claude feel sluggish. `mvn -q test` is borderline; if your suite grows, scope tests to the changed module.
- Use `.claude/settings.local.json` (gitignored) for personal-only hooks. Use `.claude/settings.json` (committed) for team-wide policy.

## How to verify

In a fresh `claude` session:

1. Ask Claude to add a trivial method to `BookService`. After it edits, you should see `mvn -q compile` run automatically in the hook output.

2. End the conversation (or wait for Claude to wrap up). The `Stop` hook should fire and run the full test suite.

3. Ask Claude to "bump Spring Boot to 3.4 in pom.xml". The `PreToolUse` hook should block the edit with your warning message; Claude should report back that it can't modify `pom.xml` without your approval.

If a hook misbehaves, run `claude --debug` to see the exact event payload and exit codes — invaluable for hook debugging.

## Related commands

| Command | What it does |
|---|---|
| `/hooks` | View hook configurations for tool events; verifies `.claude/settings.json` is loaded. |
| `/permissions` | Manage allow/ask/deny rules. Companion lever to PreToolUse blocking. |
| `/doctor` | Diagnose installation and settings; press `f` to have Claude fix issues. Useful when a hook silently fails to fire. |

See [the full command reference](https://code.claude.com/docs/en/commands) for everything else.

## Reference

- [Hooks documentation](https://docs.claude.com/claude-code/hooks)
- Compare your `settings.json` to the solution:
  ```bash
  git diff lesson-03-hooks..lesson-03-hooks-solution -- .claude/
  ```
