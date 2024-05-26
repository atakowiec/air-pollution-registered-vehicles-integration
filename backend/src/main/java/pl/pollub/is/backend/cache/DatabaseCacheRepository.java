package pl.pollub.is.backend.cache;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.pollub.is.backend.cache.model.DatabaseCache;

public interface DatabaseCacheRepository extends JpaRepository<DatabaseCache, Long> {
    void deleteByCacheKey(String key);

    @Query("SELECT c FROM DatabaseCache c WHERE c.cacheKey = :key order by c.id desc LIMIT 1")
    DatabaseCache getByCacheKey(String key);
}
