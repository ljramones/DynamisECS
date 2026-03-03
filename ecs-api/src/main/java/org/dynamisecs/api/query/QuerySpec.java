package org.dynamisecs.api.query;

import org.dynamisecs.api.component.ComponentKey;

import java.util.List;

public record QuerySpec(List<ComponentKey<?>> allOf, List<ComponentKey<?>> noneOf) { }
