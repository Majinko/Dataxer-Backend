package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;

import java.util.List;

public interface CategoryService {

    void store(Category category, Long parentId);

    Category getById(Long id);

    List<Category> all();

    List<Category> nested();

    List<Category> getChildren(Long parentId);

    void updateTree(Long parentId, Category category);

    void delete(Long id);
}
