package org.dynamisengine.ecs.core;

import org.dynamisengine.core.entity.EntityId;
import org.dynamisengine.ecs.api.component.ComponentKey;
import org.dynamisengine.ecs.api.query.Query;
import org.dynamisengine.ecs.api.query.QueryBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryTest {

    private static final ComponentKey<String> A = ComponentKey.of("A", String.class);
    private static final ComponentKey<String> B = ComponentKey.of("B", String.class);
    private static final ComponentKey<String> C = ComponentKey.of("C", String.class);

    @Test
    void queryAllOfAReturnsOnlyEntitiesWithA() {
        DefaultWorld world = new DefaultWorld();
        EntityId e1 = world.createEntity();
        EntityId e2 = world.createEntity();
        EntityId e3 = world.createEntity();

        world.add(e1, A, "a1");
        world.add(e2, B, "b2");
        world.add(e3, A, "a3");

        List<EntityId> result = toList(world.query(new QueryBuilder().allOf(A).build()));

        assertEquals(List.of(e1, e3), result);
    }

    @Test
    void queryAllOfABReturnsOnlyEntitiesWithBoth() {
        DefaultWorld world = new DefaultWorld();
        EntityId e1 = world.createEntity();
        EntityId e2 = world.createEntity();
        EntityId e3 = world.createEntity();

        world.add(e1, A, "a1");
        world.add(e2, B, "b2");
        world.add(e3, A, "a3");
        world.add(e3, B, "b3");

        List<EntityId> result = toList(world.query(new QueryBuilder().allOf(A, B).build()));

        assertEquals(List.of(e3), result);
    }

    @Test
    void queryNoneOfCExcludesEntitiesWithC() {
        DefaultWorld world = new DefaultWorld();
        EntityId e1 = world.createEntity();
        EntityId e2 = world.createEntity();
        EntityId e3 = world.createEntity();

        world.add(e1, A, "a1");
        world.add(e2, A, "a2");
        world.add(e2, C, "c2");
        world.add(e3, A, "a3");

        List<EntityId> result = toList(world.query(new QueryBuilder().allOf(A).noneOf(C).build()));

        assertEquals(List.of(e1, e3), result);
    }

    @Test
    void queryIterationIsDeterministicByEntityCreationOrder() {
        DefaultWorld world = new DefaultWorld();
        EntityId e1 = world.createEntity();
        EntityId e2 = world.createEntity();
        EntityId e3 = world.createEntity();

        world.add(e1, A, "a1");
        world.add(e2, A, "a2");
        world.add(e3, A, "a3");

        List<EntityId> result = toList(world.query(new QueryBuilder().allOf(A).build()));

        assertEquals(List.of(e1, e2, e3), result);
    }

    private static List<EntityId> toList(Query query) {
        List<EntityId> entities = new ArrayList<>();
        for (EntityId entityId : query) {
            entities.add(entityId);
        }
        return entities;
    }
}
