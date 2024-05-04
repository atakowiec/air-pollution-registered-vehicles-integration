package pl.pollub.is.backend.example;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @PostMapping
    public Product createProduct(@Valid @RequestBody CreateProductDto createProductDto) {
        return productService.createProduct(createProductDto);
    }

    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }

    @GetMapping("/by-id/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @GetMapping("/by-name/{name}")
    public List<Product> getProductsByName(@PathVariable String name) {
        return productService.getProductsByName(name);
    }
}
