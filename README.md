# learn-claude — Claude Code for Java Developers

A hands-on course that teaches Java engineers how to get real leverage out of [Claude Code](https://docs.claude.com/claude-code): project memory, slash commands, hooks, MCP servers, and subagents. Each concept is anchored to something you already know — Spring beans, AOP advice, SPI, async workers — so the learning curve is short.

## Who this is for

You write Java for a living (Spring Boot, Maven), you've poked at Claude Code or another AI coding assistant, and you want to actually customize it for your project rather than just chatting with it.

## Prerequisites

- JDK 21+
- Maven 3.9+
- Claude Code installed (`claude` on your PATH) — see [installation docs](https://docs.claude.com/claude-code/installation)
- Git

Sanity check:

```bash
mvn -v && claude --version && git --version
```

## Local development

### Start the project
```bash
mvn spring-boot:run
```

you can now visit http://localhost:8080/books

### Add a book

```bash
curl -s -X POST http://localhost:8080/books -H "Content-Type: application/json" -d '{"title":"Effective Java","author":"Joshua Bloch"}'
```

## How the repo is organized

`main` is the **baseline**: a tiny Spring Boot library service (one `Book` entity, one `BookController`, one test). It has no Claude Code customization — that's what you'll add, lesson by lesson.

Each lesson lives on its own branch. Lessons are **cumulative**: `lesson-03-hooks` already contains the `CLAUDE.md` from lesson 1 and the skills from lesson 2. To start a lesson:

```bash
git checkout lesson-01-claude-md
cat LESSON.md
```

Each lesson branch contains a `LESSON.md` with a walkthrough and a TODO checklist for you to complete. When you want to compare your work to a reference implementation, check out the matching `-solution` branch:

```bash
git checkout lesson-01-claude-md-solution
git diff lesson-01-claude-md..lesson-01-claude-md-solution
```

## Branch map

| # | Lesson | Branch | Solution |
|---|---|---|---|
| 1 | `CLAUDE.md` — project memory | `lesson-01-claude-md` | `lesson-01-claude-md-solution` |
| 2 | Skills — reusable workflows | `lesson-02-skills` | `lesson-02-skills-solution` |
| 3 | Hooks — automate around tool calls | `lesson-03-hooks` | `lesson-03-hooks-solution` |
| 4 | Tools / MCP — extend Claude with external services | `lesson-04-mcp` | `lesson-04-mcp-solution` |
| 5 | Subagents — focused workers with their own context | `lesson-05-subagents` | `lesson-05-subagents-solution` |
| 6 | Capstone — ship a feature end-to-end | `lesson-06-capstone` | `lesson-06-capstone-solution` |

## Verifying the baseline

```bash
mvn -q test
```

Should pass cleanly. If it doesn't, fix the environment before starting lesson 1.

## Acknowledgements

The tooling concepts taught here are documented at https://docs.claude.com/claude-code. This repo is the bridge from "I read the docs" to "I use it on a real Java project."
