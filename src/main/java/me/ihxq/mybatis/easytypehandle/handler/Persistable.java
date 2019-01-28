package me.ihxq.mybatis.easytypehandle.handler;

public interface Persistable<E, V> {

    V constructPersistValue();

    E parsePersistedValue(V value);
}
