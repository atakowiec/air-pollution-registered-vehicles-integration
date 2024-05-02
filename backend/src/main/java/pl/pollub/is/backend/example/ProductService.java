package pl.pollub.is.backend.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(CreateProductDto createProductDto) {
        return productRepository.save(createProductDto.toProduct());
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public Product getProduct(Long id) {
        return productRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByNameContaining(name).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }
}
