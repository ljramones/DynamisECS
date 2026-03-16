module org.dynamisengine.ecs.core {
    requires transitive org.dynamisengine.ecs.api;
    requires org.dynamisengine.core;

    exports org.dynamisengine.ecs.core;
    exports org.dynamisengine.ecs.core.query;
    exports org.dynamisengine.ecs.core.store;
}
