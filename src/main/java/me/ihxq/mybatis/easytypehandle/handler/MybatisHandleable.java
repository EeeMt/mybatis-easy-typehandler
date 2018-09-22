package me.ihxq.mybatis.easytypehandle.handler;

public interface MybatisHandleable<V, E> {
    V constructPersistValue();

    E parsePersistedValue(V persistedValue);
}
