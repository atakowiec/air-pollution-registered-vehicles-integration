package pl.pollub.is.backend.example;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateProductDto {
    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Price is mandatory")
    @Min(value = 0, message = "Price must be greater than 0")
    private Double price;

    public Product toProduct() {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        return product;
    }
}
