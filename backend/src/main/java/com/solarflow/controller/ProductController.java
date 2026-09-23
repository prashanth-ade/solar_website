package com.solarflow.controller;

import com.solarflow.model.*;
import com.solarflow.repo.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class ProductController {
    final ProductRepository p;
    final CategoryRepository c;

    ProductController(ProductRepository p, CategoryRepository c) {
        this.p = p;
        this.c = c;
    }

    @GetMapping("/products")
    List<Product> products() {
        return p.findAll();
    }

    @GetMapping("/products/{id}")
    Product product(@PathVariable Long id) {
        return p.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    @GetMapping("/categories")
    List<Category> categories() {
        return c.findAll();
    }

    @PostMapping("/admin/products")
    Product add(@RequestBody Product x) {
        return p.save(x);
    }

    @DeleteMapping("/admin/products/{id}")
    void del(@PathVariable Long id) {
        if (!p.existsById(id)) throw new IllegalArgumentException("Product not found");
        p.deleteById(id);
    }
}
