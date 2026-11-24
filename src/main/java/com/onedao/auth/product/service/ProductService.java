
package com.onedao.auth.product.service;

import com.onedao.auth.product.Product;
import com.onedao.auth.product.dto.ProductRequest;
import com.onedao.auth.product.dto.ProductResponse;
import com.onedao.auth.product.exception.ProductValidationException;
import com.onedao.auth.product.repository.ProductRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Create product
    public ProductResponse createProduct(ProductRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Product request cannot be empty");
        }

        Product product = new Product(
                request.getName(),
                request.getDescription(),
                request.getPrice()
        );

        Product savedProduct = productRepository.save(product);

        return convertToResponse(savedProduct);
    }

    // Get all products
    public List<ProductResponse> getAllProducts() {

        return productRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // Get product by ID
    public ProductResponse getProductById(Long id) {

        validateId(id);

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductValidationException(
                                "Product not found with ID: " + id));

        return convertToResponse(product);
    }

    // Update product
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        validateId(id);

        if (request == null) {
            throw new IllegalArgumentException("Product request cannot be empty");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductValidationException(
                                "Product not found with ID: " + id));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());

        Product updatedProduct = productRepository.save(product);

        return convertToResponse(updatedProduct);
    }

    // Delete product
    public String deleteProduct(Long id) {

        validateId(id);

        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductValidationException(
                                "Product not found with ID: " + id));

        productRepository.delete(product);

        return "Product deleted successfully";
    }

    // Validate product ID
    private void validateId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }
    }

    // Convert Entity to DTO
    private ProductResponse convertToResponse(Product product) {

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
