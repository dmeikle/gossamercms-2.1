package com.gossamercms.mvc.controllers;



import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.data.ListResultset;
import com.gossamercms.mvc.data.QueryOptions;
import com.gossamercms.mvc.exceptions.UnauthorizedStatusException;
import com.gossamercms.mvc.handlers.BaseHandler;

import com.gossamercms.mvc.http.ApiResponse;
import com.gossamercms.mvc.jwt.CurrentJwtUser;
import com.gossamercms.mvc.models.BaseModel;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Unmapped controller operations. Subclasses expose selected operations through
 * public, annotated endpoint methods and supply their request/response mappings.
 */
public abstract class TypedBaseController<
        EntityType extends BaseModel,
        DtoType extends DtoWithId,
        RequestType,
        ResponseType,
        ResponseListType> {

    protected final BaseHandler<EntityType, DtoType> handler;
    protected final Class<ResponseListType> responseListClass;

    protected TypedBaseController(
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


    protected ApiResponse<ResponseType> getById(DtoType existing) {
        return ApiResponse.ok(toResponse(existing));
    }

    // ---------- GET BY FILTER ----------
    protected ResponseType _get(Map<String, Object> params) {
        return toResponse(handler.get(params));
    }

    // ---------- LIST / PAGINATION ----------
    protected ListResultset<ResponseListType> _getAll(
            CurrentJwtUser jwtUser,
            int page,
            int size,
            Map<String, Object> params
    ) {
        params.remove("page");
        params.remove("size");

        QueryOptions options = QueryOptions.of(page, size, params, Map.of());

        return handler.getAll(options, responseListClass);
    }

    protected <ResultDto> ListResultset<ResultDto> _getAll(
            CurrentJwtUser jwtUser,
            int page,
            int size,
            Map<String, Object> params,
            Class<ResultDto> resultDtoClass
    ) {
        params.remove("page");
        params.remove("size");

        QueryOptions options = QueryOptions.of(page, size, params, Map.of());
        return handler.getAll(options, resultDtoClass);
    }

    // ---------- CREATE ----------
    protected ResponseType _create(
            CurrentJwtUser jwtUser,
            RequestType request
    ) {
        if(!jwtUser.isAuthenticated()) {
            throw new UnauthorizedStatusException();
        }

        return toResponse(handler.create(jwtUser.getUserId(), toDto(request)));
    }

    // ---------- UPDATE BY ID ----------
    protected ResponseType _updateById(
            CurrentJwtUser jwtUser,
            DtoType existing,
            RequestType request
    ) {
        return toResponse(handler.updateById(jwtUser.getUserId(), existing.getId(), toDto(request)));
    }

    // ---------- UPDATE BY FILTER ----------
    protected ResponseType _update(
            CurrentJwtUser jwtUser,
            RequestType request,
            Map<String, Object> params
    ) {
        return toResponse(handler.update(jwtUser.getUserId(), toDto(request), params));
    }

    // ---------- DELETE BY ID ----------
    protected void _deleteById(
            CurrentJwtUser jwtUser,
            DtoType existing
    ) {
        handler.deleteById(jwtUser.getUserId(), existing.getId());
    }

    // ---------- DELETE BY FILTER ----------
    protected void _delete(
            CurrentJwtUser jwtUser,
            Map<String, Object> params
    ) {
        handler.delete(jwtUser.getUserId(), params);
    }

    // ---------- RESTORE ----------
    protected ResponseType _restore(
            CurrentJwtUser jwtUser,
            UUID id
    ) {
        return toResponse(handler.restoreById(jwtUser.getUserId(), id));
    }

    // ---------- BULK CREATE OR REPLACE ----------
    protected ListResultset<ResponseType> _createOrReplaceBulk(
            CurrentJwtUser jwtUser,
            List<RequestType> requests,
            Map<String, Object> deleteExistingKey
    ) {
        List<DtoType> dtos = requests.stream().map(this::toDto).toList();
        return toResponseResultset(
                handler.createOrReplaceBulk(jwtUser.getUserId(), dtos, deleteExistingKey));
    }

    protected ListResultset<ResponseType> toResponseResultset(ListResultset<DtoType> result) {
        return new ListResultset<>(
                result.list().stream().map(this::toResponse).toList(),
                result.page(),
                result.pageSize(),
                result.totalCount(),
                result.totalPages()
        );
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
