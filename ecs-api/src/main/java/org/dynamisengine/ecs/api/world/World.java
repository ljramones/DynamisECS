package org.dynamisengine.ecs.api.world;

import org.dynamisengine.core.entity.EntityId;
import org.dynamisengine.ecs.api.component.ComponentKey;
import org.dynamisengine.ecs.api.query.Query;
import org.dynamisengine.ecs.api.query.QuerySpec;

import java.util.Optional;
import java.util.Set;

/**
 * ECS World contract (storage-agnostic).
 *
 * EntityId is owned by DynamisCore and must be used everywhere.
 */
public interface World {

    // ── Ticks and deltas ────────────────────────────────────────────

    WorldDelta beginTick(WorldTick tick);

    /**
     * Ends the current tick. After endTick(), the most recent delta is still available
     * until the next beginTick().
     */
    WorldDelta endTick();

    /**
     * Returns the delta for the current tick if beginTick() has been called,
     * otherwise returns the last completed tick delta (or an empty delta).
     */
    WorldDelta delta();

    // ── Entity lifecycle ─────────────────────────────────────────────

    EntityId createEntity();

    boolean destroyEntity(EntityId entityId);

    boolean exists(EntityId entityId);

    Set<EntityId> entities();

    // ── Components ──────────────────────────────────────────────────

    <T> void add(EntityId entityId, ComponentKey<T> key, T component);

    <T> Optional<T> get(EntityId entityId, ComponentKey<T> key);

    <T> boolean remove(EntityId entityId, ComponentKey<T> key);

    <T> boolean has(EntityId entityId, ComponentKey<T> key);

    Query query(QuerySpec spec);
}
