package pl.pollub.is.backend.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.pollub.is.backend.cache.model.DatabaseCache;
import pl.pollub.is.backend.cache.supplier.CacheDependency;
import pl.pollub.is.backend.cache.supplier.CacheSupplier;
import pl.pollub.is.backend.cache.supplier.PackedCacheSupplier;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DatabaseCacheService {
    private final DatabaseCacheRepository databaseCacheRepository;

    private final Map<String, PackedCacheSupplier> cacheSuppliers = new HashMap<>();

    public void registerSupplier(String key, CacheDependency dependency, CacheSupplier supplier) {
        cacheSuppliers.put(key, new PackedCacheSupplier(key, supplier, dependency));
    }

    @Transactional
    public void onDependencyChange(CacheDependency dependency) {
        for (PackedCacheSupplier packedCacheSupplier : cacheSuppliers.values()) {
            if (!packedCacheSupplier.getCacheDependency().equals(dependency)) {
                continue;
            }

            databaseCacheRepository.deleteByCacheKey(packedCacheSupplier.getKey());
        }
    }

    @Transactional
    public String getValue(String key) {
        PackedCacheSupplier packedCacheSupplier = cacheSuppliers.get(key);
        if (packedCacheSupplier == null) {
            return null;
        }

        DatabaseCache databaseCache = databaseCacheRepository.getByCacheKey(key);
        if (databaseCache == null) {
            databaseCache = new DatabaseCache();
            databaseCache.setCacheKey(key);
            databaseCache.setValue(packedCacheSupplier.getCacheSupplier().get());
            databaseCacheRepository.deleteByCacheKey(key);
            databaseCacheRepository.save(databaseCache);
        }

        return databaseCache.getValue();
    }
}
