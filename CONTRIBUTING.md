# Contributing to AI Test Failure Analyzer

Thank you for your interest in contributing! This is a community project and all contributions — bug fixes, new features, documentation improvements, and ideas — are welcome.

---

## Ways to Contribute

- **Report a bug** — open an [issue](../../issues) with steps to reproduce
- **Suggest a feature** — open a [discussion](../../discussions) or issue with your idea
- **Fix a bug or implement a feature** — open a pull request (see below)
- **Improve documentation** — README, code comments, examples, and wiki pages all count
- **Add test coverage** — help verify the tool works across different setups

---

## Development Setup

1. Fork and clone the repository
2. Make sure you have Java 17+ and Maven 3.8+
3. Set your `GROQ_API_KEY` environment variable (see README)
4. Run `mvn clean compile` to verify the build passes

---

## Pull Request Guidelines

1. **One PR per change** — keep pull requests focused and small
2. **Describe what and why** — your PR description should explain what you changed and why, not just what files you touched
3. **Don't break the interface** — `AiAnalyzer.analyzeFailure(testName, error, stackTrace)` is the public API; changes to its signature need a discussion first
4. **Add comments for non-obvious logic** — especially around the HTTP request construction and JSON parsing
5. **Test it** — run a project with a deliberately broken test and confirm the AI output appears correctly

---

## Good First Issues

New to the project? These are great starting points:

- Add a null check for `GROQ_API_KEY` with a clear error message when it's missing
- Add a configurable timeout to the OkHttpClient
- Write a JUnit 5 Extension equivalent of `AIFailureListener`
- Add support for writing AI analysis output to a file (configurable path)
- Add a second AI provider option (OpenAI or Claude) as an alternative to Groq

---

## Code Style

- Follow standard Java naming conventions
- Keep methods short and focused
- Prefer explicit variable names over abbreviations
- Add Javadoc to all public methods

---

## Questions?

Open a [GitHub Discussion](../../discussions) — happy to help you get started.
