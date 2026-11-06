package com.gossamercms.mvc.controllers;

import com.gossamercms.mvc.annotations.CurrentUser;
import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.exceptions.UnauthorizedStatusException;
import com.gossamercms.mvc.handlers.BaseHandler;

import com.gossamercms.mvc.http.ApiResponse;
import com.gossamercms.mvc.jwt.CurrentJwtUser;
import com.gossamercms.mvc.models.BaseModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class BaseController<
        EntityType extends BaseModel,
        DtoType extends DtoWithId,
        RequestType,
        ResponseType,
        ResponseListType
        > {

    protected final BaseHandler<EntityType, DtoType> handler;
    private final Class<ResponseListType> responseListClass;

    protected BaseController(
            BaseHandler<EntityType, DtoType> handler,
            Class<ResponseListType> responseListClass
    ) {
        this.handler = handler;
        this.responseListClass = responseListClass;
    }

    protected abstract DtoType toDto(RequestType request);

    protected abstract ResponseType toResponse(DtoType dto);

    protected ListResultset<ResponseType> toResponse(ListResultset<DtoType> list) {
        return new ListResultset<>(list.list().stream().map(this::toResponse).collect(Collectors.toList()),
                list.page(),list.pageSize(),list.totalCount(),list.totalPages());
    }

    @GetMapping("/{id}")
    public ApiResponse<ResponseType> getById(@PathVariable DtoType id) {
        return ApiResponse.ok(toResponse(id));
    }

    // ---------- GET BY FILTER ----------
    @GetMapping("/get")
    public ApiResponse<ResponseType> get(@RequestParam Map<String, Object> params) {
        return ApiResponse.ok(toResponse(handler.get(params)));
    }

    // ---------- LIST / PAGINATION ----------
    @GetMapping
    public ApiResponse<ListResultset<ResponseListType>> getAll(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "50") int size,
            @RequestParam Map<String, Object> params
    ) {
        params.remove("page");
        params.remove("size");

        QueryOptions options = QueryOptions.of(page, size, params, Map.of());
        return ApiResponse.ok(handler.getAll(options, responseListClass));
    }

    // ---------- CREATE ----------
    @PostMapping
    public ApiResponse<ResponseType> create(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestBody RequestType request
    ) {
        if(!jwtUser.isAuthenticated()) {
            throw new UnauthorizedStatusException();
        }

        DtoType dto = toDto(request);
        return ApiResponse.ok(toResponse(handler.create(jwtUser.getUserId(), dto)));
    }

    // ---------- UPDATE BY ID ----------
    @PutMapping("/{id}")
    public ApiResponse<ResponseType> updateById(
            @CurrentUser CurrentJwtUser jwtUser,
            @PathVariable("id") DtoType existing, //rely on the converter to preload it
            @RequestBody DtoType dto
    ) {
        return ApiResponse.ok(toResponse(handler.updateById(jwtUser.getUserId(), existing.getId(), dto)));
    }

    // ---------- UPDATE BY FILTER ----------
    @PutMapping("/update")
    public ApiResponse<ResponseType> update(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestBody DtoType dto,
            @RequestParam Map<String, Object> params
    ) {
        return ApiResponse.ok(toResponse(handler.update(jwtUser.getUserId(), dto, params)));
    }

    // ---------- DELETE BY ID ----------
    @DeleteMapping("/{id}")
    public void deleteById(
            @CurrentUser CurrentJwtUser jwtUser,
            @PathVariable("id") DtoType existing
    ) {
        handler.deleteById(jwtUser.getUserId(), existing.getId());
    }

    // ---------- DELETE BY FILTER ----------
    @DeleteMapping
    public void delete(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestParam Map<String, Object> params
    ) {
        handler.delete(jwtUser.getUserId(), params);
    }

    // ---------- RESTORE ----------
    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ResponseType> restore(
            @CurrentUser CurrentJwtUser jwtUser,
            @PathVariable UUID id
    ) {
        return ApiResponse.ok(toResponse(handler.restoreById(jwtUser.getUserId(), id)));
    }

    // ---------- BULK CREATE OR REPLACE ----------
    @PostMapping("/bulk")
    public ApiResponse<ListResultset<ResponseType>> createOrReplaceBulk(
            @CurrentUser CurrentJwtUser jwtUser,
            @RequestBody List<RequestType> requests,
            @RequestParam Map<String, Object> deleteExistingKey
    ) {
        List<DtoType> dtos = requests.stream().map(this::toDto).collect(Collectors.toList());
        return ApiResponse.ok(toResponse(handler.createOrReplaceBulk(jwtUser.getUserId(), dtos, deleteExistingKey)));
    }

    protected Map<String, String> getOrderBy(Map<String, Object> params) {

        Object sort = params.remove("sort");
        Object dir = params.remove("dir");

        params.remove("page");
        params.remove("size");

        Map<String, String> orderBy = Map.of();

        if (sort != null) {
            orderBy = Map.of(
                    sort.toString(),
                    dir == null ? "ASC" : dir.toString().toUpperCase()
            );
        }
        return orderBy;
    }

}