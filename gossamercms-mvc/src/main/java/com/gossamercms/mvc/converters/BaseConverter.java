package com.gossamercms.mvc.converters;

import tools.jackson.databind.JavaType;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.type.TypeFactory;
import tools.jackson.databind.util.Converter;
import com.gossamercms.mvc.data.BaseDbService;
import com.gossamercms.mvc.data.DtoWithId;
import com.gossamercms.mvc.exceptions.NotFoundException;
import lombok.NonNull;

import java.util.Map;
import java.util.UUID;

public abstract class BaseConverter<D extends DtoWithId> implements Converter<String, D> {

    protected final BaseDbService<?, D> dbService;

    private final Class<D> dtoClass;

    protected BaseConverter(BaseDbService<?, D> dbService, Class<D> dtoClass) {
        this.dbService = dbService;
        this.dtoClass = dtoClass;
        System.out.println("************ Converter created for " + dtoClass.getSimpleName() + " ************************");
    }

    public D convert(@NonNull String source) {
        System.out.println("************ BaseConverter loading " + dtoClass.getSimpleName() + " with id or slug " + source + " ************************");
        D dto = findByIdOrSlug(source);
        if (dto == null) {
            throw new NotFoundException(source);
        }
        return dto;
    }

    @Override
    public D convert(DeserializationContext context, String source) {
        return convert(source);
    }

    @Override
    public D convert(SerializationContext context, String source) {
        return convert(source);
    }

    private D findByIdOrSlug(String source) {
        UUID id;
        try {
            id = UUID.fromString(source);
        } catch (IllegalArgumentException ex) {
            return dbService.get(Map.of("slug", source));
        }
        return dbService.getById(id);
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(dtoClass);
    }

}
