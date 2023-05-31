package com.asteriosoft.controllers;

import com.asteriosoft.entities.Category;
import com.asteriosoft.services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {
    Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    CategoryService categoryService;

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        logger.info("GET all categories");
        return categoryService.getAll();
    }

    @GetMapping("/categories/filter")
    public ResponseEntity<List<Category>> bannerSearch(@RequestParam String searchText) {
        logger.info("GET search categories for: {}", searchText);
        return categoryService.filterByName(searchText);
    }

    @PostMapping("/category")
    public ResponseEntity<Object> createCategory(@RequestBody Object bannerObject) {
        logger.info("POST create category");
        return categoryService.create(bannerObject);
    }

    @PostMapping("/category/{id}")
    public ResponseEntity<Object> updateCategory(@PathVariable("id") Long id, @RequestBody Object categoryObject) {
        logger.info("POST update  category: {}", id);
        return categoryService.update(id, categoryObject);
    }

    @GetMapping("/category/{id}")
    public ResponseEntity<Object> deleteCategory(@PathVariable("id") long id) {
        logger.info("GET delete  category: {}", id);
        return categoryService.delete(id);
    }

}
