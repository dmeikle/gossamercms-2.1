package com.gossamercms.mvc.data;

public record QueryFilter(
        String field,
        Operator operator,
        Object value,
        String prefix
) {
    public QueryFilter(String field, Operator operator, Object value) {
        this(field, operator, value, "");
    }

    public enum Operator {
        EQUALS,
        LIKE,
        ILIKE,
        GREATER_THAN,
        GREATER_THAN_OR_EQUAL,
        LESS_THAN,
        LESS_THAN_OR_EQUAL,
        IN,
        NOT_IN,
        IS_NULL,
        IS_NOT_NULL,
        EQUALS_DATE,
        GREATER_THAN_DATE,
        GREATER_THAN_OR_EQUAL_DATE,
        LESS_THAN_DATE,
        LESS_THAN_OR_EQUAL_DATE,
        BETWEEN,
        NOT_BETWEEN,
        GREATER_THAN_TIMESTAMP,
        GREATER_THAN_OR_EQUAL_TIMESTAMP,
        LESS_THAN_TIMESTAMP,
        LESS_THAN_OR_EQUAL_TIMESTAMP,
        EQUALS_TIMESTAMP,
        NOT_EQUALS,
        NOT_EQUALS_DATE,
        NOT_EQUALS_TIMESTAMP
    }


    public static QueryFilter equals(
            String field,
            Object value
    ) {
        return new QueryFilter(
                field,
                Operator.EQUALS,
                value
        );
    }


    public static QueryFilter like(
            String field,
            String value
    ) {
        return new QueryFilter(
                field,
                Operator.LIKE,
                "%" + value + "%"
        );
    }


    public static QueryFilter ilike(
            String field,
            String value
    ) {
        return new QueryFilter(
                field,
                Operator.ILIKE,
                "%" + value + "%"
        );
    }


    public static QueryFilter isNull(
            String field
    ) {
        return new QueryFilter(
                field,
                Operator.IS_NULL,
                null
        );
    }

    @Override
    public String prefix() {
        return prefix == null || prefix.isEmpty() ? "" : prefix + ".";
    }
}