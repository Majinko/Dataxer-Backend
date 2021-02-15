package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;

import java.util.List;

public interface CategoryService {

    Category store(Category category);

    Category getById(Long id);

    List<Category> all();

    List<Category> nested();

    /*void updateTree(List<Category> categories, Category category);*/

    void delete(Long id);
}
