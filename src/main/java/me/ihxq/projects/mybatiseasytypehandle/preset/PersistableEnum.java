package me.ihxq.projects.mybatiseasytypehandle.preset;

import me.ihxq.projects.mybatiseasytypehandle.handler.Persistable;

public interface PersistableEnum<E extends Enum, V> extends Persistable<E, V> {

    V constructPersistValue();

    default E parsePersistedValue(V value) {
        throw new RuntimeException("persistable enum should parse at ");
    }
}
