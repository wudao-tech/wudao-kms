package com.wudao.kms.config;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * PostgreSQL Vector 类型处理器
 * 用于处理 pgvector 扩展的 vector 类型与 Java float[] 的转换
 */
public class VectorTypeHandler extends BaseTypeHandler<float[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, float[] parameter, JdbcType jdbcType) throws SQLException {
        PGobject pgObject = new PGobject();
        pgObject.setType("vector");
        pgObject.setValue(Arrays.toString(parameter).replace(" ", ""));
        ps.setObject(i, pgObject);
    }

    @Override
    public float[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String vectorString = rs.getString(columnName);
        return parseVector(vectorString);
    }

    @Override
    public float[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String vectorString = rs.getString(columnIndex);
        return parseVector(vectorString);
    }

    @Override
    public float[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String vectorString = cs.getString(columnIndex);
        return parseVector(vectorString);
    }

    private float[] parseVector(String vectorString) {
        if (vectorString == null) {
            return null;
        }

        // 移除方括号和空格，然后按逗号分割
        String cleanString = vectorString.replace("[", "").replace("]", "").replace(" ", "");
        String[] values = cleanString.split(",");

        float[] result = new float[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Float.parseFloat(values[i]);
        }

        return result;
    }
}