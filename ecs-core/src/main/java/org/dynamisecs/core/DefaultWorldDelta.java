package org.dynamisecs.core;

import org.dynamis.core.entity.EntityId;
import org.dynamisecs.api.component.ComponentKey;
import org.dynamisecs.api.world.WorldDelta;
import org.dynamisecs.api.world.WorldTick;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class DefaultWorldDelta implements WorldDelta {

    private static final DefaultWorldDelta EMPTY = new DefaultWorldDelta(new WorldTick(-1), true);

    private final WorldTick tick;
    private final Set<EntityId> created;
    private final Set<EntityId> destroyed;
    private final Map<ComponentKey<?>, Set<EntityId>> added;
    private final Map<ComponentKey<?>, Set<EntityId>> removed;

    public DefaultWorldDelta(WorldTick tick) {
        this(tick, false);
    }

    private DefaultWorldDelta(WorldTick tick, boolean immutable) {
        this.tick = Objects.requireNonNull(tick, "tick");
        this.created = immutable ? Set.of() : new LinkedHashSet<>();
        this.destroyed = immutable ? Set.of() : new LinkedHashSet<>();
        this.added = immutable ? Map.of() : new LinkedHashMap<>();
        this.removed = immutable ? Map.of() : new LinkedHashMap<>();
    }

    public static DefaultWorldDelta empty() {
        return EMPTY;
    }

    @Override
    public WorldTick tick() {
        return tick;
    }

    @Override
    public Set<EntityId> createdEntities() {
        return Collections.unmodifiableSet(created);
    }

    @Override
    public Set<EntityId> destroyedEntities() {
        return Collections.unmodifiableSet(destroyed);
    }

    @Override
    public Set<EntityId> componentsAdded(ComponentKey<?> key) {
        Objects.requireNonNull(key, "key");
        Set<EntityId> entities = added.get(key);
        if (entities == null) {
            return Set.of();
        }
        return Collections.unmodifiableSet(entities);
    }

    @Override
    public Set<EntityId> componentsRemoved(ComponentKey<?> key) {
        Objects.requireNonNull(key, "key");
        Set<EntityId> entities = removed.get(key);
        if (entities == null) {
            return Set.of();
        }
        return Collections.unmodifiableSet(entities);
    }

    void recordCreated(EntityId entityId) {
        created.add(entityId);
    }

    void recordDestroyed(EntityId entityId) {
        destroyed.add(entityId);
    }

    void recordAdded(ComponentKey<?> key, EntityId entityId) {
        added.computeIfAbsent(key, ignored -> new LinkedHashSet<>()).add(entityId);
    }

    void recordRemoved(ComponentKey<?> key, EntityId entityId) {
        removed.computeIfAbsent(key, ignored -> new LinkedHashSet<>()).add(entityId);
    }
}
