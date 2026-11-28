package com.gossamercms.optimization.pagination;

import java.util.List;

public record PageResponse<T>(
        List<T> data,
        PaginationMetadata pagination
) {}