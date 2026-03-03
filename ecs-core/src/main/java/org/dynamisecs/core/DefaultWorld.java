package org.dynamisecs.core;

import org.dynamis.core.entity.EntityId;
import org.dynamisecs.api.component.ComponentKey;
import org.dynamisecs.api.query.Query;
import org.dynamisecs.api.query.QuerySpec;
import org.dynamisecs.api.world.World;
import org.dynamisecs.core.query.DefaultQuery;
import org.dynamisecs.core.store.ComponentStore;
import org.dynamisecs.core.store.SparseSetStore;

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
    private long nextEntityId = 1L;

    @Override
    public EntityId createEntity() {
        while (true) {
            EntityId candidate = EntityId.of(nextEntityId++);
            if (entities.add(candidate)) {
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
        return store.remove(entityId);
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
