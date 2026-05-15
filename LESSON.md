# Lesson 3 — Hooks (automate around tool calls)

## What you'll learn

- What hooks are and which events they fire on
- How to use hooks to enforce policies and automate verification
- The difference between blocking and non-blocking hooks

## Java analogy

Hooks are **AOP for your AI assistant**. Think Spring `@Around` advice or a servlet `Filter`: a piece of code that fires *around* an event you don't own — in this case, your assistant's tool calls — letting you observe, modify, or veto.

You wouldn't dream of running a Spring app without filters for auth/logging/metrics. Once you've added hooks to an AI-assisted project, you'll feel the same way: "how did I work without auto-test-on-stop?"

## The concept

Hooks attach a shell command to lifecycle events. Both tools share the core events; the exit-code semantics are identical.

| Event | Fires when | Typical use |
|---|---|---|
| `PreToolUse` | Before a tool runs | Block/confirm risky operations |
| `PostToolUse` | After a tool succeeds | Auto-format, recompile, log |
| `Stop` | When the assistant finishes its turn | Run tests, update CI status |
| `UserPromptSubmit` | When you submit a prompt | Inject context, gate sensitive prompts |
| `PermissionRequest` *(Codex only)* | When the assistant asks for permission | Auto-approve/deny based on policy |

A hook is just a shell command. Its **exit code matters** (same convention in both tools):

- `0` → silent success
- `2` → blocking error; the message on stderr is shown to the assistant (PreToolUse) or to the user (PostToolUse)
- other → non-blocking warning, message on stderr is shown to the user

So a `PreToolUse` hook that exits 2 prevents the tool call from running. A `PostToolUse` hook that exits 2 doesn't undo the tool call (impossible) but flags the issue so the assistant can react.

### Where the config lives

| Tool | File | Format |
|---|---|---|
| **Claude Code** | `.claude/settings.json` (team) or `.claude/settings.local.json` (personal) | JSON |
| **Codex** | `.codex/config.toml` `[hooks]` section, or `.codex/hooks.json` | TOML (or JSON) |

> **Codex trust gate:** project-scoped hooks (anything under `<repo>/.codex/`) don't fire until you mark the project trusted. Run `/hooks` inside a Codex session to review and trust the project's hooks the first time you open it.

### Tiny example

**Claude Code (`.claude/settings.json`):**
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

**Codex (`.codex/config.toml`):**
```toml
[[hooks.PostToolUse]]
matcher = "Edit|Write"
[[hooks.PostToolUse.hooks]]
type = "command"
command = "mvn -q compile"
```

After every `Edit` or `Write`, Maven recompiles. If compile fails, exit code is non-zero and the assistant sees the error — it'll usually fix it on its own without you saying a word.

## Your turn (TODO)

Add a hooks config (`.claude/settings.json` for Claude, or `.codex/config.toml`/`.codex/hooks.json` for Codex) with three hooks. Each hook teaches a different pattern.

- [ ] **PostToolUse — auto-compile.** Match `Edit|Write|MultiEdit`. Run `mvn -q compile`. The point: catch breakage immediately, before the assistant moves on.

- [ ] **Stop — auto-test.** Match the `Stop` event. Run `mvn -q test`. The point: every turn ends with a green test suite or a clear failure.

- [ ] **PreToolUse — pom.xml guard.** Match `Edit|Write` *and* match the file path `pom.xml`. Echo a warning to stderr and exit `2` to block the edit. The point: enforce that pom changes get human review (you don't want the assistant bumping Spring Boot 3.3 → 4.0 without you noticing).

Hints:
- The `matcher` field is a regex over tool names. To match a path, your hook command itself reads the input from stdin (JSON containing the tool's args) — for the pom guard, write a small inline script: read stdin, check if `file_path` ends in `pom.xml`, exit accordingly.
- Keep hooks **fast**. A 30-second hook makes the assistant feel sluggish. `mvn -q test` is borderline; if your suite grows, scope tests to the changed module.
- Use `.claude/settings.local.json` (gitignored) for personal-only Claude hooks. Use `.claude/settings.json` (committed) for team-wide policy. For Codex, personal hooks go in `~/.codex/hooks.json`; project hooks go in `<repo>/.codex/`.

## How to verify

In a fresh session (`claude` or `codex`):

1. **(Codex only)** Run `/hooks` and trust the project's hooks. Without this, project hooks silently don't fire.
2. Ask the assistant to add a trivial method to `BookService`. After it edits, you should see `mvn -q compile` run automatically.
3. End the conversation (or wait for the assistant to wrap up). The `Stop` hook should fire and run the full test suite.
4. Ask the assistant to "bump Spring Boot to 3.4 in pom.xml". The `PreToolUse` hook should block the edit with your warning message; the assistant should report back that it can't modify `pom.xml` without your approval.

If a hook misbehaves, run `claude --debug` or `codex --debug` to see the exact event payload and exit codes.

## Related commands

| Claude Code | Codex | What it does |
|---|---|---|
| `/hooks` | `/hooks` | View hook configs and (in Codex) trust the project's hooks. |
| `/permissions` | `/approvals` | Manage allow/ask/deny rules. Companion lever to `PreToolUse` blocking. |
| `/doctor` | `codex doctor` | Diagnose installation and settings. Useful when a hook silently fails to fire. |

See the full command reference for [Claude Code](https://docs.claude.com/claude-code) or [Codex](https://developers.openai.com/codex/cli).

## Reference

- Claude Code hooks: <https://docs.claude.com/claude-code/hooks>
- Codex hooks: <https://developers.openai.com/codex/hooks>
- Compare your config to the solution:
  ```bash
  git diff lesson-03-hooks..lesson-03-hooks-solution -- .claude/ .codex/
  ```
