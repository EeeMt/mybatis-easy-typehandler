package me.ihxq.mybatis.easytypehandle.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
public interface MybatisHandlerToJson<E> extends MybatisHandleable<String, E> {
    Logger log = LoggerFactory.getLogger(MybatisHandlerToJson.class);

    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    default String constructPersistValue() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (Exception e) {
            log.error("erroe occurred while write value: {}", this.toString(), e);
            throw new RuntimeException(e);
        }
    }

    default E parsePersistedValue(String persistedValue) {
        try {
            //noinspection unchecked
            return OBJECT_MAPPER.readValue(persistedValue, (Class<E>) this.getClass());
        } catch (Exception e) {
            log.error("erroe occurred while write value: {}", this.toString(), e);
            throw new RuntimeException(e);
        }
    }
}
