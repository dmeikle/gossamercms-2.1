package com.gossamercms.mvc.controllers;

import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.exceptions.UnauthorizedStatusException;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.mvc.http.ApiResponse;
import com.gossamercms.mvc.jwt.CurrentJwtUser;
import com.gossamercms.mvc.models.BaseModel;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TypedBaseControllerTest {

    private final TestHandler handler = mock(TestHandler.class);
    private final CurrentJwtUser user = mock(CurrentJwtUser.class);
    private final UUID userId = UUID.randomUUID();
    private final TestDto dto = new TestDto(UUID.randomUUID(), "example");
    private final TestController controller = new TestController(handler, user);

    @Test
    void operationsAreProtectedAndHaveNoWebAnnotations() {
        for (var method : TypedBaseController.class.getDeclaredMethods()) {
            if (method.isSynthetic()) {
                continue;
            }
            assertTrue(Modifier.isProtected(method.getModifiers()), method.getName());
            assertEquals(0, method.getAnnotations().length);
            for (var annotations : method.getParameterAnnotations()) {
                assertEquals(0, annotations.length);
            }
        }
    }

    @Test
    void onlyConcreteMappingsAreExposedAndBindCustomRequest() throws Exception {
        when(user.isAuthenticated()).thenReturn(true);
        when(user.getUserId()).thenReturn(userId);
        when(handler.create(userId, new TestDto(null, "example"))).thenReturn(dto);

        var mvc = MockMvcBuilders.standaloneSetup(controller).build();
        mvc.perform(post("/typed").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"example\"}"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"success":true,"data":{"label":"example"}}
                        """));
        mvc.perform(get("/typed")).andExpect(status().isMethodNotAllowed());
        mvc.perform(get("/typed/" + dto.getId())).andExpect(status().isNotFound());
        mvc.perform(post("/typed/bulk")).andExpect(status().isNotFound());
        verify(handler).create(userId, new TestDto(null, "example"));
    }

    @Test
    void singleResultsUseResponseMapping() {
        when(user.getUserId()).thenReturn(userId);
        when(handler.get(Map.of())).thenReturn(dto);
        when(handler.updateById(userId, dto.getId(), new TestDto(null, "example"))).thenReturn(dto);
        when(handler.update(userId, new TestDto(null, "example"), Map.of())).thenReturn(dto);
        when(handler.restoreById(userId, dto.getId())).thenReturn(dto);
        var expected = new TestResponse("example");

        assertEquals(expected, controller.getById(dto).getData());
        assertEquals(expected, controller._get(Map.of()));
        assertEquals(expected, controller._updateById(user, dto, new TestRequest("example")));
        assertEquals(expected, controller._update(user, new TestRequest("example"), Map.of()));
        assertEquals(expected, controller._restore(user, dto.getId()));
    }

    @Test
    void listAndBulkMapItemsAndPreservePagination() {
        when(user.getUserId()).thenReturn(userId);
        var result = new ListResultset<>(List.of(dto), 2, 10, 25, 3);
        when(handler.getAll(any(QueryOptions.class))).thenReturn(result);
        when(handler.createOrReplaceBulk(userId, List.of(new TestDto(null, "example")), Map.of()))
                .thenReturn(result);
        var expected = new ListResultset<>(List.of(new TestResponse("example")), 2, 10, 25, 3);
        var params = new HashMap<String, Object>(Map.of("page", 2, "size", 10));

        assertEquals(expected, controller._getAll(user, 2, 10, params));
        assertTrue(params.isEmpty());
        assertEquals(expected, controller._createOrReplaceBulk(
                user, List.of(new TestRequest("example")), Map.of()));
    }

    @Test
    void customResultDtoDelegatesWithoutResponseMapping() {
        var result = new ListResultset<>(
                List.of(new CustomResult(dto.getId(), "custom")), 2, 10, 25, 3);
        var params = new HashMap<String, Object>(
                Map.of("page", 2, "size", 10, "name", "example"));
        var options = QueryOptions.of(2, 10, Map.of("name", "example"), Map.of());
        when(handler.getAll(options, CustomResult.class)).thenReturn(result);

        assertSame(result, controller._getAll(user, 2, 10, params, CustomResult.class));
        assertEquals(Map.of("name", "example"), params);
        verify(handler).getAll(options, CustomResult.class);
        verifyNoMoreInteractions(handler);
    }

    @Test
    void createRejectsUnauthenticatedUserBeforeCallingHandler() {
        assertThrows(UnauthorizedStatusException.class,
                () -> controller._create(user, new TestRequest("example")));
        verifyNoInteractions(handler);
    }

    @Test
    void deletesDelegateToHandler() {
        when(user.getUserId()).thenReturn(userId);
        controller._deleteById(user, dto);
        controller._delete(user, Map.of("name", "example"));
        verify(handler).deleteById(userId, dto.getId());
        verify(handler).delete(userId, Map.of("name", "example"));
    }

    public record TestRequest(String name) {}

    public record TestResponse(String label) {}

    record CustomResult(UUID id, String description) {}

    record TestDto(UUID getId, String name) implements DtoWithId {}

    abstract static class TestHandler extends BaseHandler<BaseModel, TestDto> {
        TestHandler() {
            super(null);
        }
    }

    @RestController
    @RequestMapping("/typed")
    static class TestController
            extends TypedBaseController<BaseModel, TestDto, TestRequest, TestResponse> {

        private final CurrentJwtUser user;

        TestController(TestHandler handler, CurrentJwtUser user) {
            super(handler);
            this.user = user;
        }

        @PostMapping
        public ApiResponse<TestResponse> createEndpoint(@RequestBody TestRequest request) {
            return ApiResponse.ok(super._create(user, request));
        }

        @Override
        protected TestDto toDto(TestRequest request) {
            return new TestDto(null, request.name());
        }

        @Override
        protected TestResponse toResponse(TestDto dto) {
            return new TestResponse(dto.name());
        }
    }
}
