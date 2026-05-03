Plan: learn-claude-for-java — Claude Code course for Java developers

 Context

 The user is competing against another AI assistant to produce a teaching repo that introduces Java developers to Claude Code's productivity surface: AGENT.md / CLAUDE.md, Skills, Hooks, Tools/MCP, and Subagents. The audience is experienced Java engineers, so each concept must be framed in terms they already know
 (AOP, DI, SPI, AsyncMethods, etc.). The repo is greenfield (empty dir, not a git repo yet).

 The user wants:
 - One lesson per branch, cumulative (each branch builds on the prior solution).
 - Each lesson = README walkthrough + hands-on TODO exercises + a paired -solution branch.
 - Stack: Spring Boot + Maven, Java 21.
 - Plan first, then implement.

 Repository shape

 learn-claude/                            (working dir)
 ├── README.md                            — course overview, prerequisites, branch map
 ├── pom.xml                              — Spring Boot 3.x parent, Java 21
 ├── src/main/java/com/learnclaude/library/
 │   ├── LibraryApplication.java
 │   ├── book/BookController.java         — GET/POST /books
 │   ├── book/BookService.java
 │   ├── book/Book.java                   — JPA entity (H2)
 │   └── book/BookRepository.java
 ├── src/main/resources/application.yml   — H2 in-memory + JPA
 └── src/test/java/com/learnclaude/library/book/BookControllerTest.java

 A small but realistic library-service surface gives every later lesson something to do: skills can scaffold a new controller, hooks can run mvn test, MCP can introspect H2, subagents can review a controller for auth gaps.

 Branch map

 main is the baseline — Spring Boot skeleton + course README. No lesson content.

 ┌──────────────────────────────┬──────────────────────────────┬───────────────────────────────────────────────────────────┐
 │            Branch            │        Branched from         │                          Purpose                          │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-01-claude-md          │ main                         │ TODOs to write CLAUDE.md                                  │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-01-claude-md-solution │ lesson-01-claude-md          │ Completed CLAUDE.md                                       │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-02-skills             │ lesson-01-claude-md-solution │ TODOs to author Skills                                    │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-02-skills-solution    │ lesson-02-skills             │ Completed .claude/skills/                                 │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-03-hooks              │ lesson-02-skills-solution    │ TODOs for hooks                                           │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-03-hooks-solution     │ lesson-03-hooks              │ Completed .claude/settings.json hooks                     │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-04-mcp                │ lesson-03-hooks-solution     │ TODOs to wire MCP                                         │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-04-mcp-solution       │ lesson-04-mcp                │ Completed .mcp.json + demo                                │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-05-subagents          │ lesson-04-mcp-solution       │ TODOs for subagents                                       │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-05-subagents-solution │ lesson-05-subagents          │ Completed .claude/agents/                                 │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-06-capstone           │ lesson-05-subagents-solution │ End-to-end "ship a feature" workflow combining everything │
 ├──────────────────────────────┼──────────────────────────────┼───────────────────────────────────────────────────────────┤
 │ lesson-06-capstone-solution  │ lesson-06-capstone           │ Completed capstone                                        │
 └──────────────────────────────┴──────────────────────────────┴───────────────────────────────────────────────────────────┘

 Cumulative chaining means lesson-05 already contains the CLAUDE.md, skills, hooks, and MCP config from lessons 1–4. The learner can git checkout lesson-N and see the world as it should look entering that lesson.

 Lesson content (file: LESSON.md at branch root)

 Each lesson follows the same template:

 1. What you'll learn — 2-3 bullet outcomes
 2. Java analogy — anchor concept to something they already know
 3. The concept — short prose with one minimal example
 4. Your turn (TODO) — checklist of concrete tasks editing files in this branch
 5. How to verify — exact commands to run / things to observe
 6. Reference — links to official Claude Code docs


  Lesson 1 — CLAUDE.md (project memory)

 - Analogy: like a README.md + CONTRIBUTING.md + style-guide rolled into one — but Claude reads it on every prompt.
 - TODOs: create CLAUDE.md with: (a) mvn commands, (b) package layout, (c) "always use Java records for DTOs" rule, (d) test conventions.
 - Verify: ask Claude "how do I run tests?" — it should answer from CLAUDE.md.

 Lesson 2 — Slash Commands / Skills

 - Analogy: like a reusable @Service bean — define once, invoke whenever you want with /skill-name.
 - TODOs: create .claude/skills/new-rest-endpoint/SKILL.md that scaffolds a controller + service + test triple following the existing book package pattern.
 - Verify: invoke /new-rest-endpoint Author and check generated files compile and tests pass.

 Lesson 3 — Hooks

 - Analogy: like Spring AOP @Around advice or a servlet Filter — fires before/after specific tool events.
 - TODOs: add .claude/settings.json with (a) PostToolUse hook running mvn -q compile after Java file edits, (b) Stop hook running mvn -q test, (c) PreToolUse hook blocking edits to pom.xml without confirmation.
 - Verify: make Claude edit a .java file — compile should run; try editing pom.xml — hook should block.

 Lesson 4 — Tools / MCP

 - Analogy: like Java SPI (ServiceLoader) — pluggable capabilities Claude discovers via a manifest.
 - TODOs: add .mcp.json configuring the filesystem MCP server (built-in, no install) scoped to src/, plus a stub for connecting an HTTP MCP server to the running Spring Boot app's /actuator endpoints.
 - Verify: ask Claude to list all @RestControllers using MCP filesystem tools rather than its own Read/Grep.

 Lesson 5 — Subagents

 - Analogy: like dispatching to a focused worker on a thread pool — fresh context window, narrow brief.
 - TODOs: create .claude/agents/spring-security-reviewer.md (reviews controllers for missing @PreAuthorize / input validation) and .claude/agents/test-writer.md (writes JUnit 5 tests using existing patterns).
 - Verify: invoke the reviewer on BookController — it should flag the unauthenticated endpoints.

 Lesson 6 — Capstone

 - Goal: add an Author feature end-to-end using everything: skill scaffolds code → hook auto-tests → subagent reviews → CLAUDE.md rules enforced → MCP filesystem queried.
 - TODOs: a single high-level TODO ("ship the Author feature using only the tooling from lessons 1-5") with a checklist the learner ticks off.

 Implementation steps (for execution phase, after approval)

 1. git init, create main with Spring Boot baseline + top-level README.md that maps the branch tree.
 2. For lessons 1-6, in order:
 a. Branch lesson-N-… from previous solution (or main for lesson 1).
 b. Add LESSON.md with concept walkthrough + TODO checklist + (where helpful) skeleton/stub files containing // TODO markers.
 c. Branch lesson-N-…-solution from lesson-N-…. Complete the TODOs. Verify build/test.
 3. Final pass: rebase-check that each lesson branch still compiles (lesson branches with TODO stubs may intentionally be incomplete — that's fine, but the solution branches must mvn -q test cleanly).
 4. Push nothing — local-only unless the user later asks for a remote.

 Critical files to create
 Verification

 End-to-end smoke test after implementation:
 - git branch --list 'lesson-*' shows 12 branches (6 lessons × 2).
 - For each *-solution branch: git checkout <branch> && mvn -q test succeeds.
 - For each lesson branch: LESSON.md exists at root and the TODO list has unchecked items.
 - The root README.md on main correctly lists every lesson with a 1-line summary and the git checkout command.

 Out of scope

 - CI/CD, GitHub Actions, publishing.
 - Hosted/public MCP servers requiring credentials.
 - Comparing Claude Code to other AI tools (user chose Claude Code-only).
 - Deep Spring/JPA tutorials — the app is a vehicle, not the subject.