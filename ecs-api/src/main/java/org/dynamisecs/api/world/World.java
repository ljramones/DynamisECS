package org.dynamisecs.api.world;

import org.dynamis.core.entity.EntityId;
import org.dynamisecs.api.component.ComponentKey;
import org.dynamisecs.api.query.Query;
import org.dynamisecs.api.query.QuerySpec;

import java.util.Optional;
import java.util.Set;

/**
 * ECS World contract (storage-agnostic).
 *
 * EntityId is owned by DynamisCore and must be used everywhere.
 */
public interface World {

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
