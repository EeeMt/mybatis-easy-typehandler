package me.ihxq.projects.mybatiseasytypehandle.handler;

public interface Persistable<E, V> {

    V constructPersistValue();

    E parsePersistedValue(V value);
}
