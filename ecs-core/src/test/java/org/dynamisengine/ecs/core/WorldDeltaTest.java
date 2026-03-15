package org.dynamisengine.ecs.core;

import org.dynamisengine.core.entity.EntityId;
import org.dynamisengine.ecs.api.component.ComponentKey;
import org.dynamisengine.ecs.api.world.WorldDelta;
import org.dynamisengine.ecs.api.world.WorldTick;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorldDeltaTest {

    private static final ComponentKey<String> A = ComponentKey.of("A", String.class);

    @Test
    void beginEndTickLifecycleRulesAreEnforced() {
        DefaultWorld world = new DefaultWorld();

        WorldDelta initialDelta = world.delta();
        assertEquals(new WorldTick(-1), initialDelta.tick());
        assertTrue(initialDelta.createdEntities().isEmpty());
        assertTrue(initialDelta.destroyedEntities().isEmpty());

        assertThrows(IllegalStateException.class, world::endTick);

        world.beginTick(new WorldTick(1));
        assertThrows(IllegalStateException.class, () -> world.beginTick(new WorldTick(2)));
        world.endTick();
    }

    @Test
    void entityCreateAndDestroyAreTrackedWithinTick() {
        DefaultWorld world = new DefaultWorld();

        world.beginTick(new WorldTick(1));
        EntityId e1 = world.createEntity();
        EntityId e2 = world.createEntity();
        world.destroyEntity(e1);
        WorldDelta delta = world.endTick();

        assertEquals(List.of(e1, e2), List.copyOf(delta.createdEntities()));
        assertEquals(List.of(e1), List.copyOf(delta.destroyedEntities()));
        assertEquals(delta, world.delta());
    }

    @Test
    void componentAddAndRemoveAreTrackedWithinTick() {
        DefaultWorld world = new DefaultWorld();

        world.beginTick(new WorldTick(2));
        EntityId entity = world.createEntity();
        world.add(entity, A, "value");
        world.remove(entity, A);
        WorldDelta delta = world.endTick();

        assertEquals(List.of(entity), List.copyOf(delta.componentsAdded(A)));
        assertEquals(List.of(entity), List.copyOf(delta.componentsRemoved(A)));
    }

    @Test
    void createdEntityOrderIsDeterministic() {
        DefaultWorld world = new DefaultWorld();

        world.beginTick(new WorldTick(3));
        EntityId e1 = world.createEntity();
        EntityId e2 = world.createEntity();
        EntityId e3 = world.createEntity();
        WorldDelta delta = world.endTick();

        assertEquals(List.of(e1, e2, e3), List.copyOf(delta.createdEntities()));
    }
}
