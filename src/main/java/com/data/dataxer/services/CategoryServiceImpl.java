package com.data.dataxer.services;

import com.data.dataxer.mappers.CategoryMapper;
import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Autowired
    EntityManager entityManager;

    @Override
    public List<CategoryDTO> nested() {
        return categoryMapper
                .toCategoryDTOs(this.categoryRepository.nested()
                .orElseThrow(() -> new RuntimeException("Categories not found")));
    }
}
