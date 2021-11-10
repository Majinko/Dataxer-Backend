package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CategoryMapper;
import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.models.dto.CategoryNestedDTO;
import com.data.dataxer.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@PreAuthorize("hasPermission(null, 'Category', 'Category')")
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

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> finById(@PathVariable Long id) {
        return ResponseEntity.ok(this.categoryMapper.toCategoryDTO(this.categoryService.findById(id)));
    }

    @GetMapping("/allByType/{type}")
    public ResponseEntity<List<CategoryDTO>> allByType(@PathVariable String type) {
        return ResponseEntity.ok(categoryMapper.toCategoryDTOs(categoryService.allByType(type)));
    }

    @PostMapping("/storeOrUpdate")
    public ResponseEntity<CategoryDTO> store(@RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.ok(categoryMapper.toCategoryDTO(this.categoryService.storeOrUpdate(categoryMapper.categoryDTOtoCategory(categoryDTO))));
    }

    @PostMapping("/updateTree")
    public void updateTree(@RequestBody List<CategoryNestedDTO> categoryDTOS) {
        categoryService.updateTree(categoryMapper.categoryNestedDTOsToCategories(categoryDTOS));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        categoryService.delete(id);
    }
}
