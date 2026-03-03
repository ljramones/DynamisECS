package org.dynamisecs.api.world;

import org.dynamis.core.entity.EntityId;
import org.dynamisecs.api.component.ComponentKey;

import java.util.Set;

public interface WorldDelta {

    WorldTick tick();

    Set<EntityId> createdEntities();

    Set<EntityId> destroyedEntities();

    /**
     * Entities that had a component added for the given key during this tick.
     */
    Set<EntityId> componentsAdded(ComponentKey<?> key);

    /**
     * Entities that had a component removed for the given key during this tick.
     */
    Set<EntityId> componentsRemoved(ComponentKey<?> key);
}
