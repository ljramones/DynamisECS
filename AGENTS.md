# Repository Guidelines

## Project Structure & Module Organization
DynamisECS is a Maven multi-module project:
- `ecs-api`: ECS contracts and public interfaces
- `ecs-core`: ECS implementation details and storage/query internals
- `ecs-runtime`: integration helpers and runtime orchestration

Standard source layout applies in each module:
- `src/main/java/...`
- `src/test/java/...`

## Build, Test, and Development Commands
- `mvn validate`: verifies aggregator/module structure and dependency graph
- `mvn test`: runs all module tests
- `mvn clean verify`: full build and verification lifecycle

Use Java `25` (preview enabled), consistent with `.java-version` and Maven compiler config.

## Coding Style & Naming Conventions
- Indentation: 4 spaces
- Encoding: UTF-8
- Package names: lowercase (`org.dynamisecs.*`)
- Types: `PascalCase`, methods/fields: `camelCase`, constants: `UPPER_SNAKE_CASE`
- Test classes: `<TypeName>Test`

Keep module boundaries strict: contracts in `ecs-api`, implementations in `ecs-core`, orchestration/integration in `ecs-runtime`.

## Testing Guidelines
- Place tests under each module’s `src/test/java`
- Prefer behavior-focused test names (for example, `createsWorldWithEmptyState`)
- Add tests for lifecycle, query semantics, and edge cases with each behavioral change

## Cross-Repo Ownership Policy
- Entity IDs must use `org.dynamis.core.entity.EntityId`
- Do not create any ECS-local `EntityId` type
- ECS defines no math types; Vectrix owns math. ECS may store Vectrix objects as component payloads only
- ECS remains independent of SceneGraph/LightEngine/Physics/AI; cross-system bridges belong in external integration modules
- Commit after each phase; do not push until explicitly requested

## Commit & Pull Request Guidelines
Use focused, imperative commits (for example, `feat: add world query contract`).

PRs should include:
- concise change summary and rationale
- linked issue/task when applicable
- validation evidence (commands run and results)
- migration notes if APIs or behavior change
