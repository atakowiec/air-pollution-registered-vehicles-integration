package pl.pollub.is.backend.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = "SELECT products.* FROM products WHERE name LIKE %:name%", nativeQuery = true)
    Optional<List<Product>> findByNameContaining(String name);
}