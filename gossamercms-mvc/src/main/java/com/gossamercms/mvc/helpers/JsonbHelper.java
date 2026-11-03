package com.gossamercms.mvc.helpers;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import org.postgresql.util.PGobject;

public class JsonbHelper {
    private static final ObjectMapper mapper = JsonMapper.builder()
            .configureForJackson2()
            .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    public static PGobject toJsonb(Object value) {
        try {
            if (value == null) return null;

            PGobject jsonObject = new PGobject();
            jsonObject.setType("jsonb");

            if (value instanceof String) {
                jsonObject.setValue((String) value);
            } else {
                jsonObject.setValue(mapper.writeValueAsString(value));
            }

            return jsonObject;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert object to JSONB", e);
        }
    }

    public static <T> T fromJsonb(Object value, Class<T> clazz) {
        try {

            if (value == null) {
                return null;
            }

            String json;

            if (value instanceof PGobject pgObject) {
                json = pgObject.getValue();
            } else {
                json = value.toString();
            }

            return mapper.readValue(json, clazz);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSONB", e);
        }
    }
}
