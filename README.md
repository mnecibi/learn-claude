# learn-ai-coding — AI-Assisted Coding for Java Developers

A hands-on course that teaches Java engineers how to get real leverage out of an AI coding assistant: project memory, slash commands / skills, hooks, MCP servers, and subagents. Each concept is anchored to something you already know — Spring beans, AOP advice, SPI, async workers — so the learning curve is short.

The course is **tool-agnostic**. Every lesson is written for both [Claude Code](https://docs.claude.com/claude-code) and [OpenAI Codex CLI](https://developers.openai.com/codex). The two assistants have near-identical feature surfaces (project memory, skills, hooks, MCP, subagents); each lesson teaches the concept once, then shows you the exact path and syntax for whichever tool you've installed.

## Who this is for

You write Java for a living (Spring Boot, Maven), you've poked at Claude Code, Codex, or another AI coding assistant, and you want to actually customize it for your project rather than just chatting with it.

## Prerequisites

- JDK 21+
- Maven 3.9+
- **At least one** of the following AI assistants:
  - Claude Code (`claude` on your PATH) — see [installation docs](https://docs.claude.com/claude-code/installation)
  - Codex CLI (`codex` on your PATH) — see [installation docs](https://developers.openai.com/codex/cli)
- Git

Sanity check (whichever you installed):

```bash
mvn -v && git --version && { claude --version || codex --version; }
```

## Pick your tool

Each lesson gives you the path/syntax for both tools side-by-side. Choose one and stick with it for the whole course, or switch back and forth — the solution branches ship configs for both, so your reference is always present.

If you've never used either, **Claude Code** uses Markdown for most config (`CLAUDE.md`, `.claude/agents/*.md`) and **Codex** uses TOML (`AGENTS.md` for memory, `.codex/config.toml` for everything else). Pick the one that matches your team's preference.

## Local development

### Start the project
```bash
mvn spring-boot:run
```

You can now visit http://localhost:8080/books

### Add a book

```bash
curl -s -X POST http://localhost:8080/books -H "Content-Type: application/json" -d '{"title":"Effective Java","author":"Joshua Bloch"}'
```

## How the repo is organized

`main` is the **baseline**: a tiny Spring Boot library service (one `Book` entity, one `BookController`, one test). It has no AI-assistant customization — that's what you'll add, lesson by lesson.

Each lesson lives on its own branch. Lessons are **cumulative**: `lesson-03-hooks` already contains the project-memory file from lesson 1 and the skills from lesson 2. To start a lesson:

```bash
git checkout lesson-01-project-memory
cat LESSON.md
```

Each lesson branch contains a `LESSON.md` with a walkthrough and a TODO checklist for you to complete. When you want to compare your work to a reference implementation, check out the matching `-solution` branch — it ships configs for **both** Claude Code and Codex so you can see how the same lesson maps to each tool:

```bash
git checkout lesson-01-project-memory-solution
git diff lesson-01-project-memory..lesson-01-project-memory-solution
```

## Branch map

| # | Lesson | Branch | Solution |
|---|---|---|---|
| 1 | Project memory (`CLAUDE.md` / `AGENTS.md`) | `lesson-01-project-memory` | `lesson-01-project-memory-solution` |
| 2 | Skills — reusable workflows | `lesson-02-skills` | `lesson-02-skills-solution` |
| 3 | Hooks — automate around tool calls | `lesson-03-hooks` | `lesson-03-hooks-solution` |
| 4 | Tools / MCP — extend your assistant with external services | `lesson-04-mcp` | `lesson-04-mcp-solution` |
| 5 | Subagents — focused workers with their own context | `lesson-05-subagents` | `lesson-05-subagents-solution` |
| 6 | Capstone — ship a feature end-to-end | `lesson-06-capstone` | `lesson-06-capstone-solution` |

## Verifying the baseline

```bash
mvn -q test
```

Should pass cleanly. If it doesn't, fix the environment before starting lesson 1.

## Acknowledgements

The tooling concepts taught here are documented at https://docs.claude.com/claude-code and https://developers.openai.com/codex. This repo is the bridge from "I read the docs" to "I use it on a real Java project."
