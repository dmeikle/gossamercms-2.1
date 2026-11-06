package com.gossamercms.mvc.controllers;

import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.handlers.BaseHandler;
import com.gossamercms.mvc.http.ApiResponse;
import com.gossamercms.mvc.jwt.CurrentJwtUser;
import com.gossamercms.mvc.models.BaseModel;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BaseControllerTest {

    private final TestHandler handler = mock(TestHandler.class);
    private final TestController controller = new TestController(handler);

    @Test
    void singleResultsStillUseResponseMapping() {
        var dto = new TestDto(UUID.randomUUID(), "example");
        var response = controller.getById(dto);

        assertTrue(response.isSuccess());
        assertEquals(new TestResponse("example"), response.getData());
        verifyNoInteractions(handler);
    }

    @Test
    void customListDelegatesDtoClassAndBypassesResponseMapping() {
        var result = new ListResultset<>(List.of(new CustomResponse("custom")), 2, 10, 25, 3);
        var options = QueryOptions.of(2, 10, Map.of("name", "example"), Map.of());
        when(handler.getAll(options, CustomResponse.class)).thenReturn(result);
        var params = new HashMap<String, Object>(
                Map.of("page", 2, "size", 10, "name", "example"));

        ApiResponse<ListResultset<CustomResponse>> response = controller.getAll(null, 2, 10, params);

        assertTrue(response.isSuccess());
        assertSame(result, response.getData());
        assertEquals(Map.of("name", "example"), params);
        verify(handler).getAll(options, CustomResponse.class);
        verifyNoMoreInteractions(handler);
    }

    @Test
    void bulkResultsStillUseResponseType() {
        var user = mock(CurrentJwtUser.class);
        var userId = UUID.randomUUID();
        when(user.getUserId()).thenReturn(userId);
        var dto = new TestDto(null, "example");
        when(handler.createOrReplaceBulk(userId, List.of(dto), Map.of()))
                .thenReturn(new ListResultset<>(List.of(dto), 1, 10, 1, 1));

        var response = controller.createOrReplaceBulk(user, List.of("example"), Map.of());

        assertTrue(response.isSuccess());
        assertEquals(new ListResultset<>(List.of(new TestResponse("example")), 1, 10, 1, 1),
                response.getData());
        verify(handler).createOrReplaceBulk(userId, List.of(dto), Map.of());
        verifyNoMoreInteractions(handler);
    }

    @Test
    void inheritedEndpointBindsPaginationAndReturnsListResponseType() throws Exception {
        var options = QueryOptions.of(2, 10, Map.of("name", "example"), Map.of());
        when(handler.getAll(options, CustomResponse.class))
                .thenReturn(new ListResultset<>(List.of(new CustomResponse("custom")), 2, 10, 25, 3));
        var userResolver = mock(HandlerMethodArgumentResolver.class);
        when(userResolver.supportsParameter(any()))
                .thenAnswer(invocation -> invocation.<MethodParameter>getArgument(0)
                        .getParameterType() == CurrentJwtUser.class);
        when(userResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(mock(CurrentJwtUser.class));

        MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(userResolver).build()
                .perform(get("/base").param("page", "2").param("size", "10")
                        .param("name", "example"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {"success":true,"data":{"list":[{"description":"custom"}],
                        "page":2,"pageSize":10,"totalCount":25,"totalPages":3}}
                        """));
        verify(handler).getAll(options, CustomResponse.class);
        verifyNoMoreInteractions(handler);
    }

    public record TestResponse(String label) {}

    public record CustomResponse(String description) {}

    record TestDto(UUID getId, String name) implements DtoWithId {}

    abstract static class TestHandler extends BaseHandler<BaseModel, TestDto> {
        TestHandler() {
            super(null);
        }
    }

    @RestController
    @RequestMapping("/base")
    static class TestController extends BaseController<BaseModel, TestDto, String, TestResponse, CustomResponse> {
        TestController(TestHandler handler) {
            super(handler, CustomResponse.class);
        }

        @Override
        protected TestDto toDto(String request) {
            return new TestDto(null, request);
        }

        @Override
        protected TestResponse toResponse(TestDto dto) {
            return new TestResponse(dto.name());
        }
    }
}
