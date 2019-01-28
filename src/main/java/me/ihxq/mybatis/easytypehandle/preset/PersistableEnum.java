package me.ihxq.mybatis.easytypehandle.preset;

import me.ihxq.mybatis.easytypehandle.handler.Persistable;

public interface PersistableEnum<E extends Enum, V> extends Persistable<E, V> {

    V constructPersistValue();

    default E parsePersistedValue(V value) {
        throw new RuntimeException("persistable enum should parse at ");
    }
}
