package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CategoryMapper;
import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.models.dto.CategoryNestedDTO;
import com.data.dataxer.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryService categoryService, CategoryMapper categoryMapper) {
        this.categoryService = categoryService;
        this.categoryMapper = categoryMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> all() {
        return ResponseEntity.ok(categoryMapper.toCategoryDTOs(categoryService.all()));
    }

    @GetMapping("/nested")
    public ResponseEntity<List<CategoryNestedDTO>> nested() {
        return ResponseEntity.ok(categoryMapper.toCategoryNestedDTOs(categoryService.nested()));
    }

    @PostMapping("/store")
    public ResponseEntity<CategoryDTO> store(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryMapper.toCategoryDTO(this.categoryService.store(categoryMapper.toCategory(categoryDTO))));
    }

    @PostMapping("/updateTree")
    public void updateTree(@RequestBody List<CategoryDTO> categoryDTOS) {
        categoryService.updateTree(categoryMapper.toCategories(categoryDTOS), null);
    }
}
