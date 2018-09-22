package me.ihxq.mybatis.easytypehandle.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@SuppressWarnings("unused")
public class AutoTypeHandler<E extends MybatisHandleable<?, E>> extends BaseTypeHandler<E> {

    private BaseTypeHandler<E> typeHandler;

    public AutoTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }

        if (MybatisHandleable.class.isAssignableFrom(type)) {
            //noinspection unchecked
            typeHandler = new PersistableHandler(type);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        typeHandler.setNonNullParameter(ps, i, parameter, jdbcType);
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return typeHandler.getNullableResult(rs, columnName);
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return typeHandler.getNullableResult(rs, columnIndex);
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return typeHandler.getNullableResult(cs, columnIndex);
    }
}
