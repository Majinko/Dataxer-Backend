package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;

import java.util.List;

public interface CategoryService {
    List<Category> all();

    Category storeOrUpdate(Category category);

    void updateTree(List<Category> categories);

    void delete(Long id);

    List<Category> allByType(String type);

    Category findById(Long id);
}
