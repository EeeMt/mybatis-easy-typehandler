package me.ihxq.projects.mybatiseasytypehandle.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@SuppressWarnings("unchecked")
@Slf4j
public class PersistableHandler<V, E extends Persistable<E, V>> extends BaseTypeHandler<E> {

    private Class<E> entityClass;
    private E instance;

    public PersistableHandler(Class<E> entityType) {
        if (entityType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.entityClass = entityType;
        try {
            if (!entityType.isEnum()) {
                instance = entityType.newInstance();
            }
        } catch (Exception e) {
            log.error("can not construct new instance of type, is there the none args constructor exist ", entityType.getSimpleName());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter.constructPersistValue());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        Object value = rs.getObject(columnName);
        return rs.wasNull() ? null : parse((V) value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        Object value = rs.getObject(columnIndex);
        return rs.wasNull() ? null : parse((V) value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        Object value = cs.getObject(columnIndex);
        return cs.wasNull() ? null : parse((V) value);
    }

    private E parse(V value) {
        try {
            if (value == null) {
                return null;
            }
            if (entityClass.isEnum()) {
                E[] constants = entityClass.getEnumConstants();
                for (E constant : constants) {
                    if (Objects.equals(constant.constructPersistValue(), value)) {
                        return constant;
                    }
                }
                log.error("Cannot convert '{}' to {} by persistence value.", value, instance.getClass().getSimpleName());
                throw new RuntimeException("unexpected value");
            }
            return instance.parsePersistedValue(value);
        } catch (Exception ex) {
            log.error("Cannot convert '{}' to {} by persistence value.", value, instance.getClass().getSimpleName(), ex);
            throw new RuntimeException("unexpected value");
        }
    }

}
