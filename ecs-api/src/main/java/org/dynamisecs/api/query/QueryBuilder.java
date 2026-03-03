package org.dynamisecs.api.query;

import org.dynamisecs.api.component.ComponentKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder for Query specifications.
 * World implementations decide how to execute.
 */
public final class QueryBuilder {

    private final List<ComponentKey<?>> all = new ArrayList<>();
    private final List<ComponentKey<?>> none = new ArrayList<>();

    public QueryBuilder allOf(ComponentKey<?>... keys) {
        for (ComponentKey<?> k : keys) {
            all.add(Objects.requireNonNull(k));
        }
        return this;
    }

    public QueryBuilder noneOf(ComponentKey<?>... keys) {
        for (ComponentKey<?> k : keys) {
            none.add(Objects.requireNonNull(k));
        }
        return this;
    }

    public QuerySpec build() {
        return new QuerySpec(List.copyOf(all), List.copyOf(none));
    }
}
