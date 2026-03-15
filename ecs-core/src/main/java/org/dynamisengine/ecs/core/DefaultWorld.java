package org.dynamisengine.ecs.core;

import org.dynamisengine.core.entity.EntityId;
import org.dynamisengine.ecs.api.component.ComponentKey;
import org.dynamisengine.ecs.api.query.Query;
import org.dynamisengine.ecs.api.query.QuerySpec;
import org.dynamisengine.ecs.api.world.World;
import org.dynamisengine.ecs.api.world.WorldDelta;
import org.dynamisengine.ecs.api.world.WorldTick;
import org.dynamisengine.ecs.core.query.DefaultQuery;
import org.dynamisengine.ecs.core.store.ComponentStore;
import org.dynamisengine.ecs.core.store.SparseSetStore;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public final class DefaultWorld implements World {

    private final LinkedHashSet<EntityId> entities = new LinkedHashSet<>();
    private final Map<ComponentKey<?>, ComponentStore<?>> stores = new HashMap<>();

    private DefaultWorldDelta currentDelta = DefaultWorldDelta.empty();
    private DefaultWorldDelta lastDelta = DefaultWorldDelta.empty();
    private boolean tickOpen;
    private long nextEntityId = 1L;

    @Override
    public WorldDelta beginTick(WorldTick tick) {
        Objects.requireNonNull(tick, "tick");
        if (tickOpen) {
            throw new IllegalStateException("Tick already open");
        }

        tickOpen = true;
        currentDelta = new DefaultWorldDelta(tick);
        return currentDelta;
    }

    @Override
    public WorldDelta endTick() {
        if (!tickOpen) {
            throw new IllegalStateException("No tick is currently open");
        }

        tickOpen = false;
        lastDelta = currentDelta;
        return lastDelta;
    }

    @Override
    public WorldDelta delta() {
        return tickOpen ? currentDelta : lastDelta;
    }

    @Override
    public EntityId createEntity() {
        while (true) {
            EntityId candidate = EntityId.of(nextEntityId++);
            if (entities.add(candidate)) {
                if (tickOpen) {
                    currentDelta.recordCreated(candidate);
                }
                return candidate;
            }
        }
    }

    @Override
    public boolean destroyEntity(EntityId entityId) {
        Objects.requireNonNull(entityId, "entityId");
        if (!entities.remove(entityId)) {
            return false;
        }
        for (ComponentStore<?> store : stores.values()) {
            store.remove(entityId);
        }
        if (tickOpen) {
            currentDelta.recordDestroyed(entityId);
        }
        return true;
    }

    @Override
    public boolean exists(EntityId entityId) {
        Objects.requireNonNull(entityId, "entityId");
        return entities.contains(entityId);
    }

    @Override
    public Set<EntityId> entities() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(entities));
    }

    @Override
    public <T> void add(EntityId entityId, ComponentKey<T> key, T component) {
        Objects.requireNonNull(entityId, "entityId");
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(component, "component");
        requireEntityExists(entityId);

        ComponentStore<T> store = getOrCreateStore(key);
        store.put(entityId, component);

        // Overwrites still count as an add event for this tick.
        if (tickOpen) {
            currentDelta.recordAdded(key, entityId);
        }
    }

    @Override
    public <T> Optional<T> get(EntityId entityId, ComponentKey<T> key) {
        Objects.requireNonNull(entityId, "entityId");
        Objects.requireNonNull(key, "key");

        ComponentStore<T> store = getStore(key);
        if (store == null || !entities.contains(entityId)) {
            return Optional.empty();
        }
        return store.get(entityId);
    }

    @Override
    public <T> boolean remove(EntityId entityId, ComponentKey<T> key) {
        Objects.requireNonNull(entityId, "entityId");
        Objects.requireNonNull(key, "key");

        ComponentStore<T> store = getStore(key);
        if (store == null || !entities.contains(entityId)) {
            return false;
        }

        boolean removed = store.remove(entityId);
        if (removed && tickOpen) {
            currentDelta.recordRemoved(key, entityId);
        }
        return removed;
    }

    @Override
    public <T> boolean has(EntityId entityId, ComponentKey<T> key) {
        Objects.requireNonNull(entityId, "entityId");
        Objects.requireNonNull(key, "key");

        ComponentStore<T> store = getStore(key);
        return store != null && entities.contains(entityId) && store.has(entityId);
    }

    @Override
    public Query query(QuerySpec spec) {
        Objects.requireNonNull(spec, "spec");
        return new DefaultQuery(this, spec);
    }

    private void requireEntityExists(EntityId entityId) {
        if (!entities.contains(entityId)) {
            throw new IllegalArgumentException("Entity does not exist: " + entityId);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> ComponentStore<T> getStore(ComponentKey<T> key) {
        return (ComponentStore<T>) stores.get(key);
    }

    @SuppressWarnings("unchecked")
    private <T> ComponentStore<T> getOrCreateStore(ComponentKey<T> key) {
        return (ComponentStore<T>) stores.computeIfAbsent(key, ignored -> new SparseSetStore<>());
    }
}
