package com.gossamercms.mvc.data;

import com.gossamercms.mvc.exceptions.ApiException;
import com.gossamercms.mvc.exceptions.DbSaveException;
import com.gossamercms.mvc.exceptions.NotFoundException;
import com.gossamercms.mvc.helpers.JsonbHelper;
import com.gossamercms.mvc.helpers.annotations.JsonColumn;
import com.gossamercms.mvc.models.BaseModel;
import com.gossamercms.mvc.models.ModelMeta;
import com.gossamercms.mvc.util.ReflectionUtils;
import org.postgresql.util.PSQLException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class BaseDbService<
        EntityType extends BaseModel,
        DtoType extends DtoWithId
        > {

    protected final DataSourceManager dsManager;
    protected final ModelMeta meta;
    protected final Class<DtoType> dtoClass;
    // Cached SQL string generated once per service lifecycle
    private final String batchUpdateSql;

    protected BaseDbService(
            Class<EntityType> modelClass,
            Class<DtoType> dtoClass,
            DataSourceManager dsManager
    ) {
        this.meta = BaseModel.metaOf(modelClass);
        this.dtoClass = dtoClass;
        this.dsManager = dsManager;
        this.batchUpdateSql = buildBatchUpdateSql(this.meta);
    }


    // ---------- Mapping ----------
    protected abstract EntityType mapToEntity(DtoType dto);
    protected abstract DtoType mapToDto(EntityType entity);
    /**
     * Constructs:
     * UPDATE users SET firstname = :firstname, lastname = :lastname, status = :status ... WHERE id = :id
     */
    private String buildBatchUpdateSql(ModelMeta meta) {
        // Exclude 'id' and read-only columns like 'createdOn' from the SET clause
        Set<String> excludedColumns = Set.of("id", "createdon", "created_on");

        String setAssignments = meta.columns().keySet().stream()
                .filter(columnName -> !excludedColumns.contains(columnName.toLowerCase()))
                .map(columnName -> String.format("%s = :%s", columnName, columnName))
                .collect(Collectors.joining(", "));

        return String.format(
                "UPDATE %s SET %s WHERE id = :id",
                meta.table(),
                setAssignments
        );
    }
    protected <OverrideDto extends DtoType> OverrideDto mapToDto(
            EntityType entity,
            Class<OverrideDto> overrideClass
    ) {
        try {
            return overrideClass.getConstructor(entity.getClass()).newInstance(entity);
        } catch (Exception e) {
            throw new RuntimeException("DTO mapping failed", e);
        }
    }

    // ---------- Filtering ----------
    protected Map<String, Object> createFilter(Map<String, Object> params) {
        Map<String, Object> validated = new LinkedHashMap<>();

        for (var entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            Class<?> expectedType = meta.columns().get(key);
            if (expectedType == null) {
                throw new IllegalArgumentException("Unknown column: " + key);
            }

            if (value == null) {
                validated.put(key, null);
                continue;
            }

            validated.put(key, convertFilterValue(expectedType, value));
        }

        return validated;
    }

    private Object convertFilterValue(Class<?> expectedType, Object value) {

        if (expectedType.isInstance(value)) {
            return value;
        }

        if (expectedType == UUID.class) {
            if (value instanceof String s) {
                return UUID.fromString(s);
            }
            return value;
        }

        if (expectedType == Instant.class && value instanceof Timestamp ts) {
            return ts.toInstant();
        }

        if ((expectedType == Boolean.class || expectedType == boolean.class)
                && value instanceof String s) {
            return Boolean.parseBoolean(s);
        }

        return value;
    }

    // ---------- Ordering ----------
    protected Map<String, String> getOrderBy() {
        return Map.of("id", "asc");
    }

    protected Map<String, String> getOrderByCustom() {
        return Map.of();
    }

    // ---------- Exclusions ----------
    protected List<String> getExcludedFields() {
        return List.of();
    }

    protected abstract DtoType removeExcludedFields(DtoType dto);

    // ---------- CRUD ----------
    public DtoType create(UUID createdBy, DtoType dto) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        EntityType entity = mapToEntity(dto);

        // Always generate a new ID
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, UUID.randomUUID());
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to auto-generate ID", e);
        }

        // createdOn
        try {
            Field createdOn = entity.getClass().getDeclaredField("createdOn");
            createdOn.setAccessible(true);
            if (createdOn.get(entity) == null) {
                createdOn.set(entity, Instant.now());
            }
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to set createdOn", e);
        }

        // createdAt
        try {
            Field createdAt = entity.getClass().getDeclaredField("createdAt");
            createdAt.setAccessible(true);
            if (createdAt.get(entity) == null) {
                createdAt.set(entity, Instant.now());
            }
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to set createdAt", e);
        }

        // updatedAt
        try {
            Field updatedAt = entity.getClass().getDeclaredField("updatedAt");
            updatedAt.setAccessible(true);
            if (updatedAt.get(entity) == null) {
                updatedAt.set(entity, Instant.now());
            }
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to set updatedAt", e);
        }

        // createdBy
        try {
            Field createdByField = entity.getClass().getDeclaredField("createdBy");
            createdByField.setAccessible(true);
            if (createdByField.get(entity) == null) {
                createdByField.set(entity, createdBy);
            }
        } catch (NoSuchFieldException ignored) {
        } catch (Exception e) {
            throw new RuntimeException("Failed to set createdBy", e);
        }

        // Build params (JSONB included)
        Map<String, Object> params = buildParamsForSave(entity);
        try {
            // Save using params
            ds.save(meta.table(), params);
        }catch (Exception e) {
            e.printStackTrace();
            throw new DbSaveException(extractSafeMessage(e));
        }
        return mapToDto(entity);
    }

    //We don't want to return the sql to the browser
    private String extractSafeMessage(Exception e) {
        Throwable cause = e;
        while (cause != null) {
            if (cause instanceof PSQLException psqlException) {
                String serverMessage = psqlException.getServerErrorMessage() != null
                        ? psqlException.getServerErrorMessage().getMessage()
                        : null;
                if (serverMessage != null) {
                    return serverMessage;
                }
            }
            cause = cause.getCause();
        }
        return e.getMessage();
    }

    protected Map<String, Object> buildParamsForSave(EntityType entity) {
        Map<String, Object> params = new HashMap<>();

        try {
            // DB column names → Java field names must match
            Map<String, Class<?>> columns = meta.columns();

            for (String columnName : columns.keySet()) {

                // Java field must have the SAME NAME as the DB column
                Field field = entity.getClass().getDeclaredField(columnName);
                field.setAccessible(true);

                Object value = field.get(entity);

                // JSONB conversion
                if (field.isAnnotationPresent(JsonColumn.class)) {
                    params.put(columnName, JsonbHelper.toJsonb(value));
                } else {
                    params.put(columnName, value);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to build params for save", e);
        }

        return params;
    }

    protected static Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }


    public DtoType getById(UUID id) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Map<String, Object> filter = Map.of("id", id);

        Map<String, Object> row = (Map) ds.findOne(meta.table(), filter);
        if (row == null) {
            throw404("id=" + id);
        }

        DtoType dto = ReflectionUtils.mapRowToDto(row, dtoClass);
        return removeExcludedFields(dto);
    }

    public DtoType get(Map<String, Object> params) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Map<String, Object> filter = createFilter(params);
        @SuppressWarnings("unchecked")
        Map<String, Object> row = (Map<String, Object>) ds.findOne(meta.table(), filter);

        if (row == null) {
            throw404(filter.toString());
        }

        DtoType dto = ReflectionUtils.mapRowToDto(row, dtoClass);
        return removeExcludedFields(dto);
    }

    public DtoType getOrNull(Map<String, Object> params) {
        try {
            return get(params);
        } catch (Exception e) {
            return null;
        }
    }

    public DtoType updateById(UUID updatedBy, DtoType dto, UUID id) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        EntityType entity = mapToEntity(dto);

        Map<String, Object> params = buildParamsForSave(entity);

        @SuppressWarnings("unchecked")
        Map<String, Object> row = (Map<String, Object>) ds.update(meta.table(), id, params);

        DtoType updatedDto = ReflectionUtils.mapRowToDto(row, dtoClass);

        return removeExcludedFields(updatedDto);
    }

    public DtoType update(UUID updatedBy, DtoType dto, Map<String, Object> params) {
        Map<String, Object> filter = createFilter(params);

        DtoType existing = get(filter);
        UUID id = existing.getId();

        return updateById(updatedBy, dto, id);
    }

    public ListResultset<DtoType> getAll(QueryOptions options) {
        return getAll(options, dtoClass);
    }

    public <ResultDto> ListResultset<ResultDto> getAll(
            QueryOptions options,
            Class<ResultDto> resultDtoClass
    ) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Map<String, Object> filter = createFilter(options.filters());
        Map<String, String> orderBy = validateOrderBy(options.orderBy());

        ListResultset<?> raw = ds.findMany(
                meta,
                options
        );

        List<Map<String, Object>> rows = (List<Map<String, Object>>) raw.list();

        List<ResultDto> dtos = rows.stream()
                .map(r -> ReflectionUtils.mapRowToDto(r, resultDtoClass))
                .toList();

        return new ListResultset<>(
                dtos,
                raw.page(),
                raw.pageSize(),
                raw.totalCount(),
                raw.totalPages()
        );
    }

    protected Map<String, String> validateOrderBy(Map<String, String> orderBy) {
        Map<String, String> validated = new LinkedHashMap<>();

        for (var entry : orderBy.entrySet()) {
            if (!meta.columns().containsKey(entry.getKey())) {
                throw new IllegalArgumentException("Unknown column in orderBy: " + entry.getKey());
            }

            String direction = entry.getValue().toLowerCase();
            if (!direction.equals("asc") && !direction.equals("desc")) {
                throw new IllegalArgumentException("Invalid sort direction: " + entry.getValue());
            }

            validated.put(entry.getKey(), direction);
        }

        return validated;
    }

//
//  //@Transactional
//  public ListResultset<DtoType> createOrReplaceBulk(
//          UUID createdBy,
//          List<DtoType> dtos,
//          Map<String, Object> deleteExistingKey,
//          boolean updateExisting) {
//
//      if (!updateExisting) {
//          // Your existing implementation:
//          // 1. Soft-delete existing rows.
//          // 2. Insert all supplied DTOs.
//          return createOrReplaceBulk(createdBy, dtos, deleteExistingKey);
//      }
//
//      List<DtoType> saved = new ArrayList<>();
//
//      // Retrieve existing records matching deleteExistingKey.
//      List<DtoType> existing = getAll(
//              QueryOptions.builder()
//                      .queryFilters(List.of(new QueryFilter(
//                              "id",
//                              QueryFilter.Operator.IN,
//                              dtos.stream().map(DtoWithId::getId).toList()
//                      )))
//                      .size(dtos.size())
//                      .build(),
//              dtoClass
//      ).list();
//
//      Map<UUID, DtoType> existingById = existing.stream()
//              .collect(Collectors.toMap(
//                      DtoType::getId,
//                      Function.identity()
//              ));
//
//      Set<UUID> processedIds = new HashSet<>();
//
//      for (DtoType dto : dtos) {
//          DtoType existingDto = dto.getId() != null
//                  ? existingById.get(dto.getId())
//                  : null;
//
//          if (existingDto != null) {
//              // Update existing record.
//              saved.add(this.updateById(createdBy, dto, existingDto.getId()));
//              processedIds.add(dto.getId());
//          } else {
//              // Insert new record.
//              saved.add(this.create(createdBy, dto));
//          }
//      }
//
//      // Soft-delete existing records that were not supplied.
//      Set<UUID> idsToDelete = existingById.keySet().stream()
//              .filter(id -> !processedIds.contains(id))
//              .collect(Collectors.toSet());
//
//      if (!idsToDelete.isEmpty()) {
//          idsToDelete.stream().forEach(id -> this.deleteById(createdBy, id));
//      }
//
//      return ListResultset.of(saved, 1, -1, (long) saved.size());
//  }
//
//    public ListResultset<DtoType> createOrReplaceBulk(
//            UUID createdBy,
//            List<DtoType> dtos,
//            Map<String, Object> deleteExistingKey) {
//
//        return createOrReplaceBulk(createdBy, dtos, deleteExistingKey, false);
//    }

    public ListResultset<DtoType> createOrReplaceBulk(
            UUID createdBy,
            List<DtoType> dtos,
            Map<String, Object> deleteExistingKey) {

        return this.createOrReplaceBulk(
                createdBy, dtos, deleteExistingKey, false
        );
    }


    public ListResultset<DtoType> createOrReplaceBulk(
            UUID createdBy,
            List<DtoType> dtos,
            Map<String, Object> deleteExistingKey,
            boolean updateExisting) {

        if (dtos == null) {
            dtos = new ArrayList<>();
        }

        if (deleteExistingKey == null) {
            deleteExistingKey = new HashMap<>();
        }

        if (updateExisting) {
            return this.createOrReplaceBulkUpdate(
                    createdBy, dtos, deleteExistingKey
            );
        }

        return this.createOrReplaceBulkReplace(
                createdBy, dtos, deleteExistingKey
        );
    }


    private ListResultset<DtoType> createOrReplaceBulkReplace(
            UUID createdBy,
            List<DtoType> dtos,
            Map<String, Object> deleteExistingKey) {

        DataSourceAdapter ds = this.dsManager.get(
                this.meta.datasourceKey()
        );

        Map<String, Object> normalizedKey = this.normalizeUuidValues(
                deleteExistingKey
        );

        if (!normalizedKey.isEmpty()) {

            String where = normalizedKey.keySet().stream()
                    .map(k -> "\"" + k + "\" = :" + k)
                    .collect(Collectors.joining(" AND "));

            Map<String, Object> params = new HashMap<>(normalizedKey);
            params.put("deletedBy", createdBy);

            String sql = """
                UPDATE "%s"
                SET "deletedAt" = now(),
                    "deletedBy" = :deletedBy
                WHERE %s
                AND "deletedAt" IS NULL
                """.formatted(this.meta.table(), where);

            ds.executeSql(sql, params);
        }

        List<DtoType> saved = new ArrayList<>();

        for (DtoType dto : dtos) {
            saved.add(this.create(createdBy, dto));
        }

        return ListResultset.of(saved, 1, -1, (long) saved.size());
    }


    private ListResultset<DtoType> createOrReplaceBulkUpdate(
            UUID createdBy,
            List<DtoType> dtos,
            Map<String, Object> deleteExistingKey) {

        List<DtoType> saved = new ArrayList<>();

        Map<String, Object> normalizedKey = this.normalizeUuidValues(
                deleteExistingKey
        );

        List<DtoType> existing = new ArrayList<>();

        if (!normalizedKey.isEmpty()) {

            List<QueryFilter> filters = normalizedKey.entrySet()
                    .stream()
                    .map(entry -> new QueryFilter(
                            entry.getKey(),
                            QueryFilter.Operator.EQUALS,
                            entry.getValue()
                    ))
                    .toList();

            existing = this.getAll(
                    QueryOptions.builder()
                            .queryFilters(filters)
                            .size(-1)
                            .build(),
                    this.dtoClass
            ).list();
        }

        Map<UUID, DtoType> existingById = existing.stream()
                .collect(Collectors.toMap(
                        dto -> ((DtoWithId) dto).getId(),
                        Function.identity()
                ));

        Set<UUID> processedIds = new HashSet<>();

        for (DtoType dto : dtos) {

            UUID id = dto.getId();

            DtoType existingDto = id != null
                    ? existingById.get(id)
                    : null;

            if (existingDto != null) {

                saved.add(this.updateById(
                        createdBy,
                        dto,
                        ((DtoWithId) existingDto).getId()
                ));

                processedIds.add(id);

            } else {

                saved.add(this.create(createdBy, dto));
            }
        }

        Set<UUID> idsToDelete = existingById.keySet()
                .stream()
                .filter(id -> !processedIds.contains(id))
                .collect(Collectors.toSet());

        for (UUID id : idsToDelete) {
            this.deleteById(createdBy, id);
        }

        return ListResultset.of(saved, 1, -1, (long) saved.size());
    }


    private Map<String, Object> normalizeUuidValues(
            Map<String, Object> values) {

        Map<String, Object> normalized = new HashMap<>();

        for (Map.Entry<String, Object> entry : values.entrySet()) {

            Object value = entry.getValue();

            if (value instanceof String s && this.isUuid(s)) {
                value = UUID.fromString(s);
            }

            normalized.put(entry.getKey(), value);
        }

        return normalized;
    }



    public void deleteById(UUID deletedBy, UUID id) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        if (!ds.exists(meta.table(), id)) {
            throw404("Record not found: " + id);
        }

        ds.delete(meta.table(), id, meta);
    }

    public void delete(UUID deletedBy, Map<String, Object> params) {
        Map<String, Object> filter = createFilter(params);

        DtoType dto = get(filter);
        UUID id = dto.getId();

        deleteById(deletedBy, id);
    }

    public DtoType restoreById(UUID restoredBy, UUID id) {
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Object restoredEntity = ds.restoreById(meta.table(), id, restoredBy);

        if (restoredEntity == null) {
            throw404("Record not found or not deleted: " + id);
        }

        return removeExcludedFields(mapToDto((EntityType) restoredEntity));
    }

    public DtoType restore(UUID restoredBy, Map<String, Object> params) {
        Map<String, Object> filter = createFilter(params);

        DtoType deletedRecord = getIncludingDeleted(filter);
        UUID id = deletedRecord.getId();

        return restoreById(restoredBy, id);
    }

    protected DtoType getIncludingDeleted(Map<String, Object> params) {
        Map<String, Object> filter = createFilter(params);

        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());

        Object entity = ds.findOneIncludingDeleted(meta.table(), filter);

        if (entity == null) {
            throw404("Record not found (including deleted): " + filter);
        }

        return removeExcludedFields(mapToDto((EntityType) entity));
    }

    /**
     * we don't always want to throw a 404
     *
     * @param params
     * @return
     */
    public DtoType optionalGet(Map<String, Object> params) {
        try {
            return get(params);
        } catch (ApiException ex) {
            if (ex.getStatus() == 404) {
                return null;
            }
            throw ex;
        }
    }

    protected  void throw404(String id) {
        throw  new NotFoundException(id);
    }

    protected Instant toInstant(Object value) {
        if (value == null) return null;
        if (value instanceof Instant i) return i;
        if (value instanceof Timestamp ts) return ts.toInstant();
        throw new IllegalArgumentException("Unsupported timestamp type: " + value.getClass());
    }

    protected boolean isUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    protected Map<String, Object> normalizeFilters(Map<String, Object> filters) {
        Map<String, Object> normalized = new HashMap<>(filters);

        for (Map.Entry<String, Object> entry : normalized.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (key.startsWith("is")) {
                if (value instanceof String s) {
                    s = s.trim().toLowerCase();
                    if (s.equals("true") || s.equals("1") || s.equals("yes")) {
                        normalized.put(key, Boolean.TRUE);
                    } else if (s.equals("false") || s.equals("0") || s.equals("no")) {
                        normalized.put(key, Boolean.FALSE);
                    } else {
                        normalized.put(key, Boolean.valueOf(s));
                    }
                } else if (value instanceof Number) {
                    normalized.put(key, ((Number) value).intValue() != 0);
                }
            }
        }

        return normalized;
    }
    /**
     * Efficiently updates a batch of DTOs in a single JDBC batch operation.
     */
    @Transactional
    public List<DtoType> updateAll(UUID updatedBy, List<DtoType> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            return Collections.emptyList();
        }
        DataSourceAdapter ds = dsManager.get(meta.datasourceKey());
        NamedParameterJdbcTemplate jdbcTemplate = ds.getNamedParameterJdbcTemplate();

        // Map DTO properties directly to SQL named parameters matching column names
        SqlParameterSource[] batchParams = dtos.stream()
                .map(dto -> {
                    BeanPropertySqlParameterSource paramSource = new BeanPropertySqlParameterSource(dto);
                    // Handle system audit metadata dynamically
                    paramSource.registerSqlType("updatedBy", java.sql.Types.OTHER);
                    return paramSource;
                })
                .toArray(SqlParameterSource[]::new);

        jdbcTemplate.batchUpdate(batchUpdateSql, batchParams);

        return dtos;
    }
}
