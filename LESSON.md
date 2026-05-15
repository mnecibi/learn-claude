# Lesson 4 — Tools / MCP (extend your assistant with external services)

## What you'll learn

- What MCP (Model Context Protocol) is and why you should care
- The difference between built-in tools, hooks, and MCP servers
- How to wire an MCP server into your project for either Claude Code or Codex

## Java analogy

MCP is **SPI for AI assistants**. In Java, `ServiceLoader` lets you plug new implementations into a running app via a manifest (`META-INF/services/...`); you don't recompile, you just drop a jar on the classpath.

MCP is the same idea: your assistant discovers MCP servers via a manifest, starts them, and exposes their tools to the model. Want it to query Postgres? Add a Postgres MCP server. Want it to file Jira tickets? Add the Atlassian MCP server. The model code doesn't change — the *tool surface* expands.

## The concept

A **tool** is anything your assistant can invoke that returns a result. Three sources:

1. **Built-in tools** (`Read`, `Write`, `Bash`, `Grep`...) — ship with the assistant.
2. **Hooks** (lesson 3) — fire as side effects of tool calls; not invoked by the model directly.
3. **MCP servers** — external processes that expose tools (and resources, prompts) over the MCP protocol. Configured per-project.

An MCP server can be a binary, a Python script, an `npx` invocation, or a remote HTTP endpoint. The protocol is transport-agnostic.

### Where the config lives

| Tool | File | Format |
|---|---|---|
| **Claude Code** | `.mcp.json` at repo root | JSON, `mcpServers` map |
| **Codex** | `.codex/config.toml` `[mcp_servers.<name>]` sections (project) or `~/.codex/config.toml` (global) | TOML — **no `.mcp.json`** |

> **Codex note:** project-scoped `.codex/config.toml` only loads after you mark the project trusted. The first time you run `codex` in the repo, it'll prompt you to trust it.

### Tiny example — `filesystem` server scoped to `./src`

**Claude Code (`.mcp.json`):**
```json
{
  "mcpServers": {
    "filesystem": {
      "command": "npx",
      "args": ["-y", "@modelcontextprotocol/server-filesystem", "./src"]
    }
  }
}
```

**Codex (`.codex/config.toml`):**
```toml
[mcp_servers.filesystem]
command = "npx"
args = ["-y", "@modelcontextprotocol/server-filesystem", "./src"]
```

After committing this, anyone running the assistant in the repo gets a `filesystem` MCP server scoped to `./src`. The assistant can now call `mcp__filesystem__list_directory`, `mcp__filesystem__read_file`, etc.

### Why use an MCP server when the assistant already has `Read` and `Grep`?

- **Scoping** — the filesystem server above is constrained to `./src`; the assistant can't accidentally read your home dir.
- **Capability** — many MCP servers expose things assistants don't have natively (database queries, Jira, Linear, your internal API).
- **Auditing** — MCP calls are first-class in transcripts; easier to review what an external integration touched.

## Your turn (TODO)

Add MCP config (`.mcp.json` for Claude, or `[mcp_servers.*]` in `.codex/config.toml` for Codex — or both) with two server entries.

- [ ] **`filesystem` server scoped to `src/`** using `@modelcontextprotocol/server-filesystem` (no install — `npx -y` runs it). The point: see how scoping replaces wide-open `Read`/`Write` for safer collaboration.

- [ ] **A stub for an HTTP MCP server** pointed at `http://localhost:8080/actuator` (Spring Boot's actuator endpoints). Use a placeholder — you don't need to actually run the actuator MCP server in this lesson; the goal is to see the shape of an HTTP-based config and learn how the assistant would call into a running Java service. A real production wire-up would expose `/actuator/metrics` and `/actuator/health` as tools.

- [ ] **Add `management.endpoints.web.exposure.include: "*"`** to `src/main/resources/application.yml` so the actuator endpoints are reachable when the app runs. (You'll also need the `spring-boot-starter-actuator` dependency in `pom.xml` — but remember, the hook from lesson 3 will block the pom edit. Confirm with the user before doing it, then approve.)

Hints:
- The config is committed (team-wide). Per-user secrets (API tokens for hosted MCP servers) belong in environment variables referenced from the config — `${ENV_VAR}` in JSON, or `env_vars = ["ENV_VAR"]` in Codex TOML.
- For Codex you can also add servers from the CLI: `codex mcp add filesystem -- npx -y @modelcontextprotocol/server-filesystem ./src`.

## How to verify

1. **Claude Code:** start a fresh `claude` session and run `claude mcp list`. **Codex:** start `codex`, run `/mcp` (or from the shell: `codex mcp list`). You should see `filesystem` (and your stub HTTP server, even if it's not actually reachable).

2. Ask your assistant: "list every `@RestController` in the project". Watch the tool calls — you want to see `mcp__filesystem__*` calls, not `Grep`/`Read` against the whole repo. The MCP server is now its preferred way of touching `src/`.

3. (Optional, if you wired the actuator MCP server for real) Run `mvn spring-boot:run` in another terminal and ask: "what's the JVM uptime?" — the actuator MCP tool should fire.

## Related commands

| Claude Code | Codex | What it does |
|---|---|---|
| `/mcp` | `/mcp` | List MCP server connections and manage OAuth. |
| `claude mcp list` | `codex mcp list` | List configured servers from the shell. |
| `/permissions` | `/approvals` | MCP tools obey the same permission rules as built-ins; this is where you allow/deny `mcp__filesystem__*`. |

See the full command reference for [Claude Code](https://docs.claude.com/claude-code) or [Codex](https://developers.openai.com/codex/cli).

## Reference

- [MCP overview](https://modelcontextprotocol.io)
- Claude Code MCP: <https://docs.claude.com/claude-code/mcp>
- Codex MCP: <https://developers.openai.com/codex/mcp>
- [MCP server registry](https://github.com/modelcontextprotocol/servers) — official servers including filesystem, postgres, github, slack, brave-search
- Compare your config to the solution:
  ```bash
  git diff lesson-04-mcp..lesson-04-mcp-solution -- .mcp.json .codex/
  ```
