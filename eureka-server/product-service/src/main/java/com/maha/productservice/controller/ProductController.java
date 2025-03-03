package com.maha.productservice.controller;

import com.maha.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.maha.productservice.entity.Product;

import java.util.List;

@RestController

@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductRepository productRepository;

    //create a prd
    @PostMapping
    public Product addProduct(@RequestBody Product product)
    {
        return productRepository.save(product);

    }

    //get all prd
    @GetMapping
    public List<Product> getAllProducts()
    {
        List<Product> products = productRepository.findAll();
        System.out.println("Fetched Products: " + products);  // Debugging Output
        return products;
    }

    //get prd  by id
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId)
    {
        Product product = productRepository.findById(productId)
                .orElseThrow(()-> new RuntimeException("Product not found with ID:" + productId));
        return ResponseEntity.ok(product);
    }

}


