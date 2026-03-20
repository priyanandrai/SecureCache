# Contributing to SecureCache

Thanks for improving SecureCache.

This project implements the paper model **Dynamic Key Camouflage Encryption** (IJFMR260271936), so contributions should preserve both API behavior and cryptographic flow.

## Code of Conduct

All contributors are expected to follow [`CODE_OF_CONDUCT.md`](CODE_OF_CONDUCT.md).
Report unacceptable behavior to **amitrai5100@gmail.com**.

## Before You Start

- Read [`README.md`](README.md), [`AGENTS.md`](AGENTS.md), and [`SECURITY.md`](SECURITY.md).
- Use Java 8+ and Maven.
- Prefer a clean validation run: `mvn clean test`.

## Local Development Workflow

1. Fork and branch from `main`.
2. Make focused changes.
3. Run tests locally:
   - `mvn clean test`
4. Open a PR with context and rationale.

## What to Include in a Pull Request

- Clear title and concise change summary.
- Why the change is needed (bug, paper alignment, security, performance, maintainability).
- Files/modules touched.
- Validation evidence (test output, reproducible steps, or both).
- Compatibility notes if data format/API behavior changed.

## Bug Reports

Use the GitHub **Bug report** template and include:

- SecureCache version or commit hash
- Minimal reproducible code sample
- Expected vs actual behavior
- Java, Maven, and OS versions

Do **not** use public issues for vulnerabilities. See Security below.

## Feature Requests

Use the GitHub **Feature request** template and describe:

- Problem statement
- Proposed API/behavior
- Alternatives considered
- If relevant, paper alignment (section reference from IJFMR260271936)

## Security Reports

For vulnerabilities, do not open a public issue.
Report privately to **amitrai5100@gmail.com**.

See [`SECURITY.md`](SECURITY.md) for expected response policy.

## Project-Specific Conventions

- Public API currently favors null/boolean fallbacks over rich exceptions.
- `SourcesLoader` package and builder method naming are intentionally legacy (`Loader(...)`).
- `JumbleFunctionInterface` canonical methods are `jumbleData` / `reassemble` with legacy aliases retained.
- Keep envelope compatibility in mind: `Constant.CACHE_VERSION` gates format changes.

## Testing Expectations

- Keep and extend coverage in existing suites:
  - `CipherPaperModelTest`
  - `JumbleFunctionRoundtripTest`
  - `SecureCacheCallFunctionalityTest`
  - `SecureCacheConcurrencyTest`
  - `TimeMapTest`
- Add or update tests for any behavior change.
- Avoid flaky tests and timing assumptions unless unavoidable.

## License

By contributing, you agree your contribution is licensed under the Apache License 2.0 used by this repository.
