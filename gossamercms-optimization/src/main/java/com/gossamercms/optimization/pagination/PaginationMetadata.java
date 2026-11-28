package com.gossamercms.optimization.pagination;

public record PaginationMetadata(
        int page,
        int size,
        long total,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {}