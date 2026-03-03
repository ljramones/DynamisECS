package org.dynamisecs.api.query;

import org.dynamis.core.entity.EntityId;
import org.dynamisecs.api.component.ComponentKey;

import java.util.Iterator;
import java.util.List;

/**
 * Query contract for iterating entities that match component presence.
 *
 * Implementations are free to optimize (sparse sets, archetypes, etc.).
 */
public interface Query extends Iterable<EntityId> {

    List<ComponentKey<?>> allOf();

    List<ComponentKey<?>> noneOf();

    @Override
    Iterator<EntityId> iterator();
}
