package org.dynamisengine.ecs.core.query;

import org.dynamisengine.core.entity.EntityId;
import org.dynamisengine.ecs.api.component.ComponentKey;
import org.dynamisengine.ecs.api.query.Query;
import org.dynamisengine.ecs.api.query.QuerySpec;
import org.dynamisengine.ecs.core.DefaultWorld;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public final class DefaultQuery implements Query {

    private final DefaultWorld world;
    private final List<ComponentKey<?>> allOf;
    private final List<ComponentKey<?>> noneOf;

    public DefaultQuery(DefaultWorld world, QuerySpec spec) {
        this.world = Objects.requireNonNull(world, "world");
        Objects.requireNonNull(spec, "spec");
        this.allOf = List.copyOf(spec.allOf());
        this.noneOf = List.copyOf(spec.noneOf());
    }

    @Override
    public List<ComponentKey<?>> allOf() {
        return allOf;
    }

    @Override
    public List<ComponentKey<?>> noneOf() {
        return noneOf;
    }

    @Override
    public Iterator<EntityId> iterator() {
        Iterator<EntityId> base = world.entities().iterator();
        return new Iterator<>() {
            private EntityId next;

            @Override
            public boolean hasNext() {
                if (next != null) {
                    return true;
                }
                while (base.hasNext()) {
                    EntityId candidate = base.next();
                    if (matches(candidate)) {
                        next = candidate;
                        return true;
                    }
                }
                return false;
            }

            @Override
            public EntityId next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                EntityId current = next;
                next = null;
                return current;
            }
        };
    }

    private boolean matches(EntityId entityId) {
        for (ComponentKey<?> key : allOf) {
            if (!has(entityId, key)) {
                return false;
            }
        }
        for (ComponentKey<?> key : noneOf) {
            if (has(entityId, key)) {
                return false;
            }
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    private boolean has(EntityId entityId, ComponentKey<?> key) {
        return world.has(entityId, (ComponentKey<Object>) key);
    }
}
