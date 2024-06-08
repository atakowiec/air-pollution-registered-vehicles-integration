package pl.pollub.is.backend.cache.supplier;

import lombok.Data;

@Data
public class PackedCacheSupplier {
    private final String key;
    private final CacheSupplier cacheSupplier;
    private final CacheDependency[] cacheDependencies;
}
