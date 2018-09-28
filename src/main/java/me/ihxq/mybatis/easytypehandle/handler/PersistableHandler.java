package me.ihxq.mybatis.easytypehandle.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})
public class PersistableHandler<V, E extends MybatisHandleable<V, E>> extends BaseTypeHandler<E> {

    private Class<E> classType;
    private Class<V> valueType;

    private boolean isHandleable(Type type) {
        ParameterizedType parameterizedType;
        if (type instanceof ParameterizedType) {
            parameterizedType = (ParameterizedType) type;
        } else {
            return false;
        }
        return MybatisHandleable.class.isAssignableFrom((Class<?>) parameterizedType.getRawType());
    }

    private Class findValueTypeInInterfaceSign(Class classType) {
        Type[] genericInterfaces = classType.getGenericInterfaces(); // implemented interfaces
        for (Type genericInterface : genericInterfaces) {
            if (isHandleable(genericInterface)) {
                Type[] genericTypes = ((ParameterizedType) genericInterface).getActualTypeArguments();
                if (genericTypes != null && genericTypes.length == 2) { //because MybatisHandleable has 2 generic type
                    //noinspection unchecked
                    return (Class<V>) genericTypes[0]; // fist is the value generic type
                }
            }
        }
        for (Type genericInterface : genericInterfaces) {
            ParameterizedType parameterizedType;
            if (genericInterface instanceof ParameterizedType) {
                parameterizedType = (ParameterizedType) genericInterface;
            } else {
                continue;
            }
            Type rawType = parameterizedType.getRawType();
            Class supClass = (Class) rawType;
            Class find = findValueTypeInInterfaceSign(supClass);
            if (find != null) {
                return find;
            }
        }
        return null;
    }

    public PersistableHandler(Class<E> classType) {
        if (classType == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        //noinspection unchecked
        Class<V> valueTypeInInterfaceSign = findValueTypeInInterfaceSign(classType);

        if (valueTypeInInterfaceSign == null) {
            log.error("Generic type argument should be assigned: {}", classType.getName());
            throw new IllegalArgumentException("Generic type argument should be assigned");
        }
        this.valueType = valueTypeInInterfaceSign;
        this.classType = classType;
    }


    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        ps.setObject(i, parameter.constructPersistValue());
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        V value;
        try {
            value = rs.getObject(columnName, valueType);
        } catch (Exception e) {
            //noinspection unchecked
            value = (V) rs.getObject(columnName);
        }
        return rs.wasNull() ? null : parse(value);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        V value;
        try {
            value = rs.getObject(columnIndex, valueType);
        } catch (Exception e) {
            //noinspection unchecked
            value = (V) rs.getObject(columnIndex);
        }
        return rs.wasNull() ? null : parse(value);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        V value;
        try {
            value = cs.getObject(columnIndex, valueType);
        } catch (Exception e) {
            //noinspection unchecked
            value = (V) cs.getObject(columnIndex);
        }
        return cs.wasNull() ? null : parse(value);
    }

    private E parse(V persistedValue) {
        try {
            return classType.newInstance().parsePersistedValue(persistedValue);
        } catch (Exception e) {
            log.error("error occurred while parsing: {} into {}", persistedValue, classType.getSimpleName());
            throw new RuntimeException(e);
        }
    }

}
