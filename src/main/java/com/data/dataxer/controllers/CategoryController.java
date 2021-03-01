package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CategoryMapper;
import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.models.dto.CategoryNestedDTO;
import com.data.dataxer.models.dto.CategoryStoreDTO;
import com.data.dataxer.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @PostMapping("/store")
    public void store(@RequestBody CategoryStoreDTO categoryStoreDTO) {
        this.categoryService.store(this.resolveParentId(categoryStoreDTO.getParent()), categoryStoreDTO.getName());
    }

    @GetMapping("/all")
    public ResponseEntity<List<CategoryDTO>> all() {
        return ResponseEntity.ok(categoryMapper.toCategoryDTOs(categoryService.all()));
    }

    @Transactional
    @GetMapping("/nested")
    public ResponseEntity<List<CategoryNestedDTO>> nested() {
        return ResponseEntity.ok(categoryService.nested(null));
    }

    @GetMapping("/updateTree")
    public void updateTree(@RequestParam(value = "id", defaultValue = "-1") Long parentId, @RequestBody CategoryDTO categoryDTO) {
        categoryService.updateTree(parentId, categoryMapper.toCategory(categoryDTO), categoryDTO.getId());
    }

    @GetMapping("/children")
    public ResponseEntity<List<CategoryDTO>> getChildren(@RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(this.categoryMapper.toCategoryDTOs(this.categoryService.getChildren(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryMapper.toCategoryDTO(this.categoryService.getById(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        categoryService.delete(id);
    }

    private Long resolveParentId(CategoryDTO categoryDTO) {
        if (categoryDTO != null) {
            return categoryDTO.getId();
        } else {
            return null;
        }
    }

}
