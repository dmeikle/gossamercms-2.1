package com.gossamercms.optimization.pagination;


public class PaginationValidator {

    private final int defaultSize;
    private final int maximumSize;

    public PaginationValidator(
            int defaultSize,
            int maximumSize
    ) {
        this.defaultSize = defaultSize;
        this.maximumSize = maximumSize;
    }

    public int normalizePage(int page) {
        return Math.max(1, page);
    }

    public int normalizeSize(int size) {
        if (size == -1) {
            return -1;
        }

        if (size <= 0) {
            return defaultSize;
        }

        return Math.min(size, maximumSize);
    }

    public boolean isUnlimited(int size) {
        return size == -1;
    }
}
