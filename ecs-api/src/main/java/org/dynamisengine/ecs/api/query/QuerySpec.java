package org.dynamisengine.ecs.api.query;

import org.dynamisengine.ecs.api.component.ComponentKey;

import java.util.List;

public record QuerySpec(List<ComponentKey<?>> allOf, List<ComponentKey<?>> noneOf) { }
