package org.dynamisengine.ecs.api;

import org.dynamisengine.ecs.api.component.ComponentKey;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComponentKeyTest {

    @Test
    void blankIdThrows() {
        assertThrows(IllegalArgumentException.class, () -> ComponentKey.of(" ", String.class));
    }

    @Test
    void nullTypeThrows() {
        assertThrows(NullPointerException.class, () -> ComponentKey.of("position", null));
    }

    @Test
    void equalityIsBasedOnIdOnly() {
        ComponentKey<String> one = ComponentKey.of("position", String.class);
        ComponentKey<Integer> two = ComponentKey.of("position", Integer.class);

        assertEquals(one, two);
        assertEquals(one.hashCode(), two.hashCode());
    }
}
