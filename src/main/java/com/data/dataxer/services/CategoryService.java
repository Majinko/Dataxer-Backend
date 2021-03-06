package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;

import java.util.List;

public interface CategoryService {
    List<Category> all();

    List<Category> nested();

    Category store(Category category);

    void updateTree(List<Category> categories, Category category);

    void delete(Long id);
}
