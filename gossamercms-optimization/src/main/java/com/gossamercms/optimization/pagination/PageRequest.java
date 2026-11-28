package com.gossamercms.optimization.pagination;

public record PageRequest(
        int page,
        int size
) {}