package pl.pollub.is.backend.cache.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cache")
@Data
public class DatabaseCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cache_key", unique = true)
    private String cacheKey;

    @Column(name = "value", columnDefinition = "TEXT")
    private String value;
}
