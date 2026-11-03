package com.gossamercms.mvc.util;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.core.JacksonException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;


public class JsonUtil {

    public static final ObjectMapper OBJECT_MAPPER =
            JsonMapper.builder()
                    .configureForJackson2()
                    .build();

    private JsonUtil() {}

    public static <T> T toObject(ResultSet rs,
                                 String column,
                                 Class<T> type)
            throws SQLException {

        String json = rs.getString(column);

        if (json == null || json.isBlank()) {
            return null;
        }

        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JacksonException e) {
            e.printStackTrace();
            throw new SQLException(
                    "Unable to deserialize JSON column '" + column + "'", e);
        }
    }

    public static <T> List<T> toList(ResultSet rs,
                                     String column,
                                     TypeReference<List<T>> type)
            throws SQLException {

        String json = rs.getString(column);

        if (json == null || json.isBlank()) {
            return Collections.emptyList();
        }

        try {
            return OBJECT_MAPPER.readValue(json, type);
        } catch (JacksonException e) {
            e.printStackTrace();
            throw new SQLException(
                    "Unable to deserialize JSON column '" + column + "'", e);
        }
    }

    public static <T> List<T> toList(ResultSet rs,
                                     String column,
                                     Class<T> elementType)
            throws SQLException {

    String json = rs.getString(column);

    if (json == null || json.isBlank()) {
        return Collections.emptyList();
    }

    try {
        return OBJECT_MAPPER.readValue(
                json,
                OBJECT_MAPPER.getTypeFactory()
                        .constructCollectionType(List.class, elementType));
    } catch (JacksonException e) {
        e.printStackTrace();
        throw new SQLException(
                "Unable to deserialize JSON column '" + column + "'", e);
    }

    }
}