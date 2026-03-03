package org.dynamisecs.api.component;

import java.util.Objects;

/**
 * Typed component identity used to register and access component stores.
 *
 * The key is stable and comparable by id.
 * Do not embed engine subsystems here (SceneGraph/Physics/AI).
 */
public final class ComponentKey<T> {

    private final String id;
    private final Class<T> type;

    private ComponentKey(String id, Class<T> type) {
        this.id = id;
        this.type = type;
    }

    public static <T> ComponentKey<T> of(String id, Class<T> type) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ComponentKey id must not be null/blank");
        }
        Objects.requireNonNull(type, "type");
        return new ComponentKey<>(id, type);
    }

    public String id() {
        return id;
    }

    public Class<T> type() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ComponentKey<?> other)) {
            return false;
        }
        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "ComponentKey[" + id + ", " + type.getSimpleName() + "]";
    }
}
