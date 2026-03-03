package org.dynamisecs.core.store;

import org.dynamis.core.entity.EntityId;

import java.util.Optional;

public interface ComponentStore<T> {

    void put(EntityId entityId, T component);

    Optional<T> get(EntityId entityId);

    boolean remove(EntityId entityId);

    boolean has(EntityId entityId);

    int size();
}
