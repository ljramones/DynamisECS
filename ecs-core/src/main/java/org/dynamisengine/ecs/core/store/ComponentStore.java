package org.dynamisengine.ecs.core.store;

import org.dynamisengine.core.entity.EntityId;

import java.util.Optional;

public interface ComponentStore<T> {

    void put(EntityId entityId, T component);

    Optional<T> get(EntityId entityId);

    boolean remove(EntityId entityId);

    boolean has(EntityId entityId);

    int size();
}
