package com.data.dataxer.controllers;

import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/nested")
    public ResponseEntity<List<CategoryDTO>> nested() {
        return ResponseEntity.ok(categoryService.nested());
    }
}
