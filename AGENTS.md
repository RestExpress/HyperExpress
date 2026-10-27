# Repository Guidelines

## Project Structure & Module Organization

HyperExpress is a Java 17 Maven multi-module project. `core/` contains shared resource, link-building, token-binding, and expansion APIs. `hal/` and `siren/` provide the HAL and Siren representations and Jackson serialization. `restexpress/` integrates these facilities with RestExpress. Production code is under each module’s `src/main/java`; tests and fixtures are under `src/test/java`. `docs/` contains project diagrams and images, while the root `pom.xml` defines the reactor and shared build configuration.

## Build, Test, and Development Commands

Run commands from the repository root:

- `mvn test` compiles all modules and runs the full test suite.
- `mvn -pl core test` runs the core module tests; use `-pl hal` or `-pl siren` to target another module.
- `mvn clean verify` performs a clean build and verification across the reactor. Verify may require signing configuration because artifact signing is bound to that lifecycle.

Use a JDK 17 installation, as configured by the parent POM.

## Coding Style & Naming Conventions

Follow the surrounding Java style: tab indentation is used in the Maven POMs; Java files generally use conventional Java formatting. Keep packages lowercase and aligned with existing module namespaces. Use `UpperCamelCase` for classes and test classes, `lowerCamelCase` for methods and fields, and descriptive test names ending in `Test`. Keep changes scoped to the appropriate module and preserve public API compatibility unless a change explicitly requires otherwise. No repository-wide formatter or linter configuration is present.

## Testing Guidelines

Tests use JUnit 4 and live alongside their module under `src/test/java`, following the production package structure. Add or update tests for behavior changes, including serialization and integration paths where applicable. Run `mvn test` before submitting; when changing shared `core` behavior, test dependent modules as well.

## Commit & Pull Request Guidelines

Recent commits use short, imperative summaries (for example, “Updated README” and “Fixed issue in MapStringFormat…”). Follow that concise style and focus each commit on one change. Pull requests should explain the motivation and implementation, link a relevant issue when available, and include test results. Call out compatibility or API changes explicitly; include screenshots only when changing visual documentation or assets.
