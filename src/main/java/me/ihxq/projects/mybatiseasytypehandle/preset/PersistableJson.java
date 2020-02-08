package me.ihxq.projects.mybatiseasytypehandle.preset;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.ihxq.projects.mybatiseasytypehandle.handler.Persistable;

public interface PersistableJson<E> extends Persistable<E, String> {

    @JsonIgnore
    default String constructPersistValue() {
        try {
            return this.getObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @JsonIgnore
    default E parsePersistedValue(String value) {
        try {
            //noinspection unchecked
            return (E) this.getObjectMapper().readValue(value, this.getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @JsonIgnore
    default ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }
}
