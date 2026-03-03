package org.dynamisecs.core.store;

import org.dynamis.core.entity.EntityId;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Sparse-set style component store backed by dense arrays.
 *
 * This v1 implementation uses boxed map keys for correctness and simplicity.
 */
public final class SparseSetStore<T> implements ComponentStore<T> {

    private static final int DEFAULT_CAPACITY = 16;

    private final Map<Long, Integer> indexByEntityId = new HashMap<>();
    private EntityId[] denseEntities = new EntityId[DEFAULT_CAPACITY];
    private Object[] denseComponents = new Object[DEFAULT_CAPACITY];
    private int size;

    @Override
    public void put(EntityId entityId, T component) {
        Objects.requireNonNull(entityId, "entityId");
        Objects.requireNonNull(component, "component");

        Integer existingIndex = indexByEntityId.get(entityId.id());
        if (existingIndex != null) {
            denseComponents[existingIndex] = component;
            return;
        }

        ensureCapacity(size + 1);
        denseEntities[size] = entityId;
        denseComponents[size] = component;
        indexByEntityId.put(entityId.id(), size);
        size++;
    }

    @Override
    public Optional<T> get(EntityId entityId) {
        Objects.requireNonNull(entityId, "entityId");
        Integer index = indexByEntityId.get(entityId.id());
        if (index == null) {
            return Optional.empty();
        }
        @SuppressWarnings("unchecked")
        T component = (T) denseComponents[index];
        return Optional.of(component);
    }

    @Override
    public boolean remove(EntityId entityId) {
        Objects.requireNonNull(entityId, "entityId");
        Integer index = indexByEntityId.remove(entityId.id());
        if (index == null) {
            return false;
        }

        int lastIndex = size - 1;
        if (index != lastIndex) {
            EntityId movedEntity = denseEntities[lastIndex];
            Object movedComponent = denseComponents[lastIndex];

            denseEntities[index] = movedEntity;
            denseComponents[index] = movedComponent;
            indexByEntityId.put(movedEntity.id(), index);
        }

        denseEntities[lastIndex] = null;
        denseComponents[lastIndex] = null;
        size--;
        return true;
    }

    @Override
    public boolean has(EntityId entityId) {
        Objects.requireNonNull(entityId, "entityId");
        return indexByEntityId.containsKey(entityId.id());
    }

    @Override
    public int size() {
        return size;
    }

    private void ensureCapacity(int requiredSize) {
        if (requiredSize <= denseEntities.length) {
            return;
        }

        int newCapacity = Math.max(requiredSize, denseEntities.length * 2);
        EntityId[] newDenseEntities = new EntityId[newCapacity];
        Object[] newDenseComponents = new Object[newCapacity];

        System.arraycopy(denseEntities, 0, newDenseEntities, 0, size);
        System.arraycopy(denseComponents, 0, newDenseComponents, 0, size);

        denseEntities = newDenseEntities;
        denseComponents = newDenseComponents;
    }
}
