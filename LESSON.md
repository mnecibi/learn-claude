# Lesson 4 — Tools / MCP (extend Claude with external services)

## What you'll learn

- What MCP (Model Context Protocol) is and why you should care
- The difference between built-in tools, hooks, and MCP servers
- How to wire an MCP server into a Claude Code project

## Java analogy

MCP is **SPI for AI assistants**. In Java, `ServiceLoader` lets you plug new implementations into a running app via a manifest (`META-INF/services/...`); you don't recompile, you just drop a jar on the classpath.

MCP is the same idea: a Claude Code session discovers MCP servers via a manifest (`.mcp.json`), starts them, and exposes their tools to the model. Want Claude to query Postgres? Add a Postgres MCP server. Want it to file Jira tickets? Add the Atlassian MCP server. The model code doesn't change — the *tool surface* expands.

## The concept

A **tool** is anything Claude can invoke that returns a result. Three sources:

1. **Built-in tools** (`Read`, `Write`, `Bash`, `Grep`...) — ship with Claude Code.
2. **Hooks** (lesson 3) — fire as side effects of tool calls; not invoked by the model directly.
3. **MCP servers** — external processes that expose tools (and resources, prompts) over the MCP protocol. Configured per-project in `.mcp.json`.

An MCP server can be a binary, a Python script, an `npx` invocation, or a remote HTTP endpoint. The protocol is transport-agnostic.

### Tiny example — `.mcp.json`

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

After committing this, anyone running `claude` in the repo gets a `filesystem` MCP server scoped to `./src`. Claude can now call `mcp__filesystem__list_directory`, `mcp__filesystem__read_file`, etc.

### Why use an MCP server when Claude already has `Read` and `Grep`?

- **Scoping** — the filesystem server above is constrained to `./src`; Claude can't accidentally read your home dir.
- **Capability** — many MCP servers expose things Claude doesn't have natively (database queries, Jira, Linear, your internal API).
- **Auditing** — MCP calls are first-class in transcripts; easier to review what an external integration touched.

## Your turn (TODO)

Add an `.mcp.json` at the repo root with two server entries.

- [ ] **`filesystem` server scoped to `src/`** using `@modelcontextprotocol/server-filesystem` (no install — `npx -y` runs it). The point: see how scoping replaces wide-open `Read`/`Write` for safer collaboration.

- [ ] **A stub for an HTTP MCP server** pointed at `http://localhost:8080/actuator` (Spring Boot's actuator endpoints). Use a placeholder — you don't need to actually run the actuator MCP server in this lesson; the goal is to see the shape of an HTTP-based config and learn how Claude would call into a running Java service. Add a `// note:` in the LESSON about how a real production wire-up would expose `/actuator/metrics`, `/actuator/health` to Claude as tools.

- [ ] **Add `management.endpoints.web.exposure.include: "*"`** to `src/main/resources/application.yml` so the actuator endpoints are reachable when the app runs. (You'll also need the `spring-boot-starter-actuator` dependency in `pom.xml` — but remember, the hook from lesson 3 will block the pom edit. Confirm with the user before doing it, then approve.)

Hints:
- `.mcp.json` is committed (team-wide). Per-user secrets (API tokens for hosted MCP servers) belong in environment variables referenced from the JSON via `${ENV_VAR}`.
- Check what's available: `claude mcp list` shows configured servers; `claude mcp tools <server>` lists the tools each one exposes.

## How to verify

1. Start a fresh `claude` session and run `claude mcp list`. You should see `filesystem` (and your stub HTTP server, even if it's not actually reachable).

2. Ask Claude: "list every `@RestController` in the project". Watch the tool calls — you want to see `mcp__filesystem__*` calls, not `Grep`/`Read` against the whole repo. The MCP server is now its preferred way of touching `src/`.

3. (Optional, if you wired the actuator MCP server for real) Run `mvn spring-boot:run` in another terminal and ask Claude: "what's the JVM uptime?" — it should call the actuator MCP tool.

## Related commands

| Command | What it does |
|---|---|
| `/mcp` | Manage MCP server connections and OAuth. Replaces the `claude mcp list` reference above. |
| `/permissions` | MCP tools obey the same permission rules as built-ins; this is where you allow/deny `mcp__filesystem__*`. |

See [the full command reference](https://code.claude.com/docs/en/commands) for everything else.

## Reference

- [MCP overview](https://modelcontextprotocol.io)
- [Configuring MCP in Claude Code](https://docs.claude.com/claude-code/mcp)
- [MCP server registry](https://github.com/modelcontextprotocol/servers) — official servers including filesystem, postgres, github, slack, brave-search
- Compare your `.mcp.json` to the solution:
  ```bash
  git diff lesson-04-mcp..lesson-04-mcp-solution
  ```
