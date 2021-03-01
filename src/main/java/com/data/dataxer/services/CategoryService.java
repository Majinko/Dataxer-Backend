package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.dto.CategoryNestedDTO;

import java.util.List;

public interface CategoryService {

    void store(Long parentId, String name);

    Category getById(Long id);

    List<Category> all();

    List<CategoryNestedDTO> nested(List<Category> categories);

    List<Category> getChildren(Long parentId);

    void updateTree(Long parentId, Category category, Long categoryId);

    void delete(Long id);
}
