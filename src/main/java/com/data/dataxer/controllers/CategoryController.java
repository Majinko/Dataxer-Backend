package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CategoryMapper;
import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.models.dto.CategoryNestedDTO;
import com.data.dataxer.services.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/category")
//@PreAuthorize("hasPermission(null, 'Category', 'Category')")
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

    @GetMapping("/allUserCategoryByTime")
    public ResponseEntity<List<CategoryDTO>> allUserCategoryByTime(
            @RequestParam(value = "uid") String uid
    ) {
        return ResponseEntity.ok(categoryMapper.toCategoryDTOs(this.categoryService.allUserCategoryByTime(uid)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> finById(@PathVariable Long id) {
        return ResponseEntity.ok(this.categoryMapper.toCategoryDTO(this.categoryService.findById(id)));
    }

    @GetMapping("/allByGroupFromParent/{group}")
    public ResponseEntity<List<CategoryDTO>> allByGroupFromParent(@PathVariable String group) {
        return ResponseEntity.ok(this.categoryMapper.toCategoryDTOs(categoryService.allByGroupFromParent(group)));
    }

    @GetMapping("/allByType/{type}")
    public ResponseEntity<List<CategoryDTO>> allByType(@PathVariable String type) {
        return ResponseEntity.ok(categoryMapper.toCategoryDTOs(categoryService.allByType(type)));
    }

    @GetMapping("/allByTypes")
    public ResponseEntity<List<CategoryDTO>> allByTypes(
            @RequestParam(value = "types") String types
    ) {
        return ResponseEntity.ok(categoryMapper.toCategoryDTOs(categoryService.allByTypes(Arrays.asList(types.split(",")))));
    }

    @GetMapping("/allByGroups")
    public ResponseEntity<List<CategoryDTO>> allByGroups(
            @RequestParam(value = "groups") String groups
    ) {
        return ResponseEntity.ok(categoryMapper.toCategoryDTOs(categoryService.allByGroups(Arrays.asList(groups.split(",")))));
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
