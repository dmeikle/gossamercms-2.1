package com.gossamercms.optimization.caching;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * One module's cache file. {@code match} chooses the file for a request.
 * {@code paths} are the individual endpoints inside that module.
 */
@Data
public class CacheModuleFile {

    private String name;

    private String match;

    private List<CacheProperties.Path> paths = new ArrayList<>();
}
