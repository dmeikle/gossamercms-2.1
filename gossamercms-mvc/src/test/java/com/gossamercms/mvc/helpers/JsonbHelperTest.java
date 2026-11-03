package com.gossamercms.mvc.helpers;

import com.gossamercms.mvc.util.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.postgresql.util.PGobject;
import tools.jackson.databind.JsonNode;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonbHelperTest {

    @Test
    void roundTripsJavaTimeUsingIso8601Jsonb() {
        TimestampValue value = new TimestampValue(Instant.parse("2026-11-03T19:00:00Z"));
        PGobject jsonb = JsonbHelper.toJsonb(value);

        assertThat(jsonb.getType()).isEqualTo("jsonb");
        assertThat(jsonb.getValue()).isEqualTo("{\"occurredAt\":\"2026-11-03T19:00:00Z\"}");
        assertThat(JsonbHelper.fromJsonb(jsonb, TimestampValue.class)).isEqualTo(value);
        assertThat(JsonbHelper.fromJsonb(jsonb.getValue(), TimestampValue.class)).isEqualTo(value);
    }

    @Test
    void convertsDatabaseJsonToJackson3TreeAndMap() {
        PGobject jsonb = JsonbHelper.toJsonb(Map.of("name", "test"));

        Object tree = ReflectionUtils.convertFromDatabase(JsonNode.class, jsonb);
        assertThat(tree).isInstanceOf(JsonNode.class);
        assertThat(((JsonNode) tree).get("name").asString()).isEqualTo("test");
        assertThat(ReflectionUtils.convertFromDatabase(Map.class, jsonb))
                .isEqualTo(Map.of("name", "test"));
    }

    @Test
    void preservesRawJsonAndNulls() {
        assertThat(JsonbHelper.toJsonb("{\"name\":\"test\"}").getValue())
                .isEqualTo("{\"name\":\"test\"}");
        assertThat(JsonbHelper.toJsonb(null)).isNull();
        assertThat(JsonbHelper.fromJsonb(null, TimestampValue.class)).isNull();
    }

    @Test
    void rejectsMalformedJsonb() {
        assertThatThrownBy(() -> JsonbHelper.fromJsonb("{", TimestampValue.class))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to parse JSONB")
                .hasCauseInstanceOf(tools.jackson.core.JacksonException.class);
    }

    public record TimestampValue(Instant occurredAt) {}
}
