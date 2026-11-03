package com.gossamercms.mvc.converters;

import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.exceptions.NotFoundException;
import com.gossamercms.mvc.models.BaseModel;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class BaseConverterTest {

    private final TestDbService db = mock(TestDbService.class);
    private final BaseConverter<TestDto> converter = new BaseConverter<>(db, TestDto.class) {};
    private final UUID id = UUID.randomUUID();
    private final TestDto dto = new TestDto(id);

    @Test
    void uuidLoadsById() {
        when(db.getById(id)).thenReturn(dto);

        assertSame(dto, converter.convert(id.toString()));

        verify(db).getById(id);
        verifyNoMoreInteractions(db);
    }

    @Test
    void nonUuidLoadsBySlug() {
        when(db.get(Map.of("slug", "my-page"))).thenReturn(dto);

        assertSame(dto, converter.convert("my-page"));

        verify(db).get(Map.of("slug", "my-page"));
        verifyNoMoreInteractions(db);
    }

    @Test
    void missingUuidDoesNotFallBackToSlug() {
        var failure = new NotFoundException(id.toString());
        when(db.getById(id)).thenThrow(failure);

        assertSame(failure, assertThrows(NotFoundException.class, () -> converter.convert(id.toString())));

        verify(db).getById(id);
        verifyNoMoreInteractions(db);
    }

    @Test
    void missingSlugPreservesNotFound() {
        var failure = new NotFoundException("missing");
        when(db.get(Map.of("slug", "missing"))).thenThrow(failure);

        assertSame(failure, assertThrows(NotFoundException.class, () -> converter.convert("missing")));
    }

    @Test
    void nullResultsAreNotFound() {
        assertThrows(NotFoundException.class, () -> converter.convert(id.toString()));
        assertThrows(NotFoundException.class, () -> converter.convert("missing"));
    }

    @Test
    void lookupErrorsAreNotTreatedAsParsingFailuresOrNotFound() {
        var failure = new IllegalArgumentException("Invalid database mapping");
        when(db.getById(id)).thenThrow(failure);
        when(db.get(Map.of("slug", "my-page"))).thenThrow(failure);

        assertSame(failure, assertThrows(IllegalArgumentException.class,
                () -> converter.convert(id.toString())));
        assertSame(failure, assertThrows(IllegalArgumentException.class,
                () -> converter.convert("my-page")));

        verify(db).getById(id);
        verify(db).get(Map.of("slug", "my-page"));
        verifyNoMoreInteractions(db);
    }

    record TestDto(UUID getId) implements DtoWithId {}

    abstract static class TestDbService extends BaseDbService<BaseModel, TestDto> {
        TestDbService() {
            super(BaseModel.class, TestDto.class, null);
        }
    }
}
