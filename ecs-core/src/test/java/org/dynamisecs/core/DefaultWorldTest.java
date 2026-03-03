package org.dynamisecs.core;

import org.dynamis.core.entity.EntityId;
import org.dynamisecs.api.component.ComponentKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DefaultWorldTest {

    private static final ComponentKey<Integer> HEALTH = ComponentKey.of("health", Integer.class);

    @Test
    void createEntityProducesUniqueIdsAndExistsTrue() {
        DefaultWorld world = new DefaultWorld();

        EntityId a = world.createEntity();
        EntityId b = world.createEntity();

        assertNotEquals(a, b);
        assertTrue(world.exists(a));
        assertTrue(world.exists(b));
    }

    @Test
    void destroyEntityRemovesEntityAndComponentsNoLongerAccessible() {
        DefaultWorld world = new DefaultWorld();
        EntityId entity = world.createEntity();
        world.add(entity, HEALTH, 10);

        assertTrue(world.destroyEntity(entity));
        assertFalse(world.exists(entity));
        assertTrue(world.get(entity, HEALTH).isEmpty());
        assertFalse(world.has(entity, HEALTH));
        assertFalse(world.remove(entity, HEALTH));
    }

    @Test
    void addGetHasRemoveBehaviorForSingleComponentType() {
        DefaultWorld world = new DefaultWorld();
        EntityId entity = world.createEntity();

        world.add(entity, HEALTH, 42);

        assertTrue(world.has(entity, HEALTH));
        assertEquals(42, world.get(entity, HEALTH).orElseThrow());
        assertTrue(world.remove(entity, HEALTH));
        assertFalse(world.has(entity, HEALTH));
        assertTrue(world.get(entity, HEALTH).isEmpty());
    }

    @Test
    void addOverwritesExistingComponentForSameKey() {
        DefaultWorld world = new DefaultWorld();
        EntityId entity = world.createEntity();

        world.add(entity, HEALTH, 10);
        world.add(entity, HEALTH, 20);

        assertEquals(20, world.get(entity, HEALTH).orElseThrow());
    }

    @Test
    void addToUnknownEntityThrows() {
        DefaultWorld world = new DefaultWorld();
        EntityId unknown = EntityId.of(999);

        assertThrows(IllegalArgumentException.class, () -> world.add(unknown, HEALTH, 1));
    }
}
