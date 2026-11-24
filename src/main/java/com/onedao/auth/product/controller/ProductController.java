package com.onedao.auth.product.controller;

import com.onedao.auth.product.dto.ProductRequest;
import com.onedao.auth.product.dto.ProductResponse;
import com.onedao.auth.product.service.ProductService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Create product
    @PostMapping
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request) {

        return productService.createProduct(request);
    }

    // Get all products
    @GetMapping
    public List<ProductResponse> getAllProducts() {

        return productService.getAllProducts();
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ProductResponse getProductById(
            @PathVariable Long id) {

        return productService.getProductById(id);
    }

    // Update product
    @PutMapping("/{id}")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    // Delete product
    @DeleteMapping("/{id}")
    public String deleteProduct(
            @PathVariable Long id) {

        return productService.deleteProduct(id);
    }
}