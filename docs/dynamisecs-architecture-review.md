# DynamisECS Architecture Boundary Ratification Review

Date: 2026-03-09

## Intent and Scope

This is a **boundary-ratification review** for DynamisECS based on current repository code and layout.

This pass does not refactor code. It establishes strict ownership and dependency boundaries so later reviews (`DynamisSceneGraph`, `DynamisLightEngine`, `DynamisWorldEngine`, `DynamisSession`, `DynamisScripting`) can resolve overlap consistently.

## 1) Repo Overview (Grounded)

Repository shape:

- Multi-module Maven project:
  - `ecs-api`
  - `ecs-core`
  - `ecs-runtime`
- Current `ecs-runtime` is skeletal only (`.gitkeep`), with no implemented runtime integration code.

Current implemented packages:

- API (`org.dynamisengine.ecs.api.*`)
  - component key (`ComponentKey`)
  - world contracts (`World`, `WorldDelta`, `WorldTick`)
  - query contracts (`Query`, `QuerySpec`, `QueryBuilder`)
- Core (`org.dynamisengine.ecs.core.*`)
  - `DefaultWorld`
  - `DefaultWorldDelta`
  - `DefaultQuery`
  - storage interfaces/impl (`ComponentStore`, `SparseSetStore`)

Dependencies:

- `dynamis-core` only (compile), plus JUnit test deps.
- No compile dependency on `DynamisEvent`, `DynamisWorldEngine`, `DynamisSceneGraph`, `DynamisSession`, `DynamisScripting`, `DynamisLightEngine`.

## 2) Strict Ownership Statement

### 2.1 What DynamisECS should exclusively own

DynamisECS should own **generic ECS state/model execution substrate**, specifically:

- component identity and typed component-keying in ECS scope (`ComponentKey`)
- entity/component storage semantics and implementation strategies
- world-local entity lifecycle APIs (`create`, `destroy`, `exists`, component attach/remove/get/has)
- component query semantics (`allOf`/`noneOf` matching)
- ECS-local tick delta model (`WorldDelta` as storage mutation delta)

### 2.2 What is appropriate for ECS

Appropriate concerns include:

- data-oriented component storage and lookup
- query and iteration contracts
- ECS-internal mutation and delta tracking
- implementation choice for storage (sparse set / archetype / table) as internal strategy

### 2.3 What DynamisECS must never own

DynamisECS must not own:

- authoritative world simulation policy
- gameplay orchestration and system scheduling policy
- session ownership/persistence/network authority
- scene graph ownership or spatial hierarchy policy
- scripting policy
- rendering/light-engine policy
- feature-domain rules (AI/audio/physics/gameplay semantics)

## 3) Dependency Rules

### 3.1 Allowed dependencies for DynamisECS

- `DynamisCore` identity/base contracts (current behavior)
- JDK collections/concurrency primitives

### 3.2 Forbidden dependencies for DynamisECS

- `DynamisWorldEngine` orchestration modules
- `DynamisSession` authority/persistence layers
- `DynamisSceneGraph` spatial ownership layers
- `DynamisScripting` policy/law layers
- `DynamisLightEngine` rendering/planning layers
- Feature modules (`DynamisAI`, `DynamisAudio`, etc.)

### 3.3 Who may depend on DynamisECS

- orchestration/integration layers that host ECS world instances (`WorldEngine`, runtime composition layers)
- feature systems that need ECS read/write/query capability, via ECS abstractions

Dependency direction intent:

- ECS remains a generic substrate under policy/orchestration layers, not a policy layer itself.

## 4) Public vs Internal Boundary Assessment

### 4.1 Canonical public API

Current intended public contracts appear in `ecs-api`:

- `org.dynamisengine.ecs.api.component.*`
- `org.dynamisengine.ecs.api.world.*`
- `org.dynamisengine.ecs.api.query.*`

These are appropriate as external-facing ECS contracts.

### 4.2 Internal implementation areas

Current implementation classes in `ecs-core`:

- `DefaultWorld`
- `DefaultWorldDelta`
- `DefaultQuery`
- store implementations

Boundary concern:

- `DefaultWorld` is currently public and directly constructible.
- This is practical for now, but it risks downstream coupling to one concrete storage/runtime strategy if no stable factory/runtime abstraction is introduced later.

### 4.3 `ecs-runtime` status

- `ecs-runtime` currently has no implementation.
- This means runtime/orchestration-facing ECS boundary is not yet concretely established.

## 5) Policy Leakage / Overlap Findings

### 5.1 Major clean boundaries confirmed

- No world/session/scripting/scene/render policy logic is present in current code.
- Storage/query/tick-delta concerns are mostly ECS-local and generic.
- No dependency coupling to SceneGraph, Session, LightEngine, Scripting, or Event bus implementation.

### 5.2 Key overlap and ambiguity signals

1. **World ownership naming tension**
- API type is named `World` and implementation `DefaultWorld`.
- In ecosystem terms, `WorldEngine` and `Session` are also “world authority” candidates.
- Risk: consumers may treat ECS `World` as canonical gameplay/world authority rather than ECS state substrate.

2. **Tick semantics in ECS API**
- `beginTick/endTick/delta` exist at ECS world level.
- This is acceptable for ECS-local mutation tracking, but could drift into engine-wide scheduling policy if expanded.

3. **No explicit system execution contract in API**
- Repo currently models storage/query/tick-delta but not a formal ECS system contract/scheduler boundary.
- This is not a defect, but it leaves open where system orchestration belongs (should be higher-level, not ECS by default).

4. **Runtime module is skeletal**
- `ecs-runtime` currently does not define integration boundaries.
- This is a current ambiguity: future runtime integration could either remain clean or absorb policy concerns.

## 6) Ratification Result

**Ratified with constraints**.

Why:

- Current implemented code is largely ECS-substrate-focused and avoids obvious policy leakage.
- Constraints are required because naming (`World`) + tick APIs + missing runtime boundary could lead to downstream boundary confusion if not explicitly governed.

## 7) Strict Boundary Rules to Carry Forward

1. Treat `ecs-api` as canonical public contract; keep policy out.
2. Keep `ecs-core` implementation replaceable; avoid freezing concrete strategy as de facto required API.
3. Keep tick semantics ECS-local (mutation window/delta), not global engine orchestration policy.
4. Keep world/session/scene/render/script authority above ECS in integration layers.
5. Do not add persistence/network/session authority logic directly into ECS.

## 8) Recommended Next Step

Next deep review should be **DynamisSceneGraph**.

Reason:

- SceneGraph is the nearest likely overlap boundary with ECS state ownership (entities/transforms/spatial authority).
- Clarifying ECS vs SceneGraph ownership now reduces later ambiguity in LightEngine and WorldEngine integration.
