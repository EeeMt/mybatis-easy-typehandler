package me.ihxq.mybatis.easytypehandle.preset;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.ihxq.mybatis.easytypehandle.exception.PrerequisitesUnsatisfiedException;
import me.ihxq.mybatis.easytypehandle.handler.MybatisHandleable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

public interface Jsonable<E> extends MybatisHandleable<String, E> {
    Logger log = LoggerFactory.getLogger(Jsonable.class);

    @Component
    class ObjectMapperHolder {

        private static ObjectMapper objectMapper;

        static ObjectMapper getObjectMapper() {
            return objectMapper;
        }

        @Resource
        public void setObjectMapper(ObjectMapper objectMapper) {
            if (objectMapper == null) {
                throw new PrerequisitesUnsatisfiedException("required a bean matches type of ObjectMapper");
            }
            ObjectMapperHolder.objectMapper = objectMapper;
        }
    }

    default String constructPersistValue() {
        try {
            return ObjectMapperHolder.getObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            log.error("error occurred while write value: {}", this.toString(), e);
            throw new RuntimeException(e);
        }
    }

    default E parsePersistedValue(String persistedValue) {
        try {
            //noinspection unchecked
            return ObjectMapperHolder.getObjectMapper().readValue(persistedValue, (Class<E>) this.getClass());
        } catch (Exception e) {
            log.error("erroe occurred while write value: {}", this.toString(), e);
            throw new RuntimeException(e);
        }
    }
}
