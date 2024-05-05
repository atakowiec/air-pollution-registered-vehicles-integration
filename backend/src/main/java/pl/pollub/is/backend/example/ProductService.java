package pl.pollub.is.backend.example;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.pollub.is.backend.exception.HttpException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

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
        return productRepository.findById(id).orElseThrow(() -> new HttpException(404, "Product not found"));
    }

    public List<Product> getProductsByName(String name) {
        return productRepository.findByNameContaining(name).orElseThrow(() -> new HttpException(404, "Product not found"));
    }
}
