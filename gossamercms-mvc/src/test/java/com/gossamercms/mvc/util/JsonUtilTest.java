package com.gossamercms.mvc.util;

import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JsonUtilTest {

    private final ResultSet rs = mock(ResultSet.class);

    @Test
    void readsObjectsAndBothListForms() throws SQLException {
        when(rs.getString("object")).thenReturn("{\"name\":\"test\"}");
        when(rs.getString("list")).thenReturn("[{\"name\":\"test\"}]");
        Entry entry = new Entry("test");

        assertThat(JsonUtil.toObject(rs, "object", Entry.class)).isEqualTo(entry);
        assertThat(JsonUtil.toList(rs, "list", Entry.class)).containsExactly(entry);
        assertThat(JsonUtil.toList(rs, "list", new TypeReference<List<Entry>>() {}))
                .containsExactly(entry);
    }

    @Test
    void preservesEmptyColumnBehavior() throws SQLException {
        when(rs.getString("blank")).thenReturn(" ");

        assertThat(JsonUtil.toObject(rs, "missing", Entry.class)).isNull();
        assertThat(JsonUtil.toObject(rs, "blank", Entry.class)).isNull();
        assertThat(JsonUtil.toList(rs, "missing", Entry.class)).isEmpty();
        assertThat(JsonUtil.toList(rs, "blank", new TypeReference<List<Entry>>() {})).isEmpty();
    }

    @Test
    void wrapsJackson3ParsingFailuresInSqlExceptions() throws SQLException {
        when(rs.getString("invalid")).thenReturn("{");

        assertThatThrownBy(() -> JsonUtil.toObject(rs, "invalid", Entry.class))
                .isInstanceOf(SQLException.class)
                .hasMessageContaining("invalid")
                .hasCauseInstanceOf(tools.jackson.core.JacksonException.class);
        assertThatThrownBy(() -> JsonUtil.toList(rs, "invalid", Entry.class))
                .isInstanceOf(SQLException.class);
        assertThatThrownBy(() -> JsonUtil.toList(rs, "invalid", new TypeReference<List<Entry>>() {}))
                .isInstanceOf(SQLException.class);
    }

    @Test
    void retainsTimestampSerializationForDatabaseJsonUtility() {
        assertThat(JsonUtil.OBJECT_MAPPER.writeValueAsString(Instant.EPOCH))
                .isEqualTo("0.0");
    }

    public record Entry(String name) {}
}
