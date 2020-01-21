package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Category> all() {
        return categoryRepository
                .findAllByDeletedAtIsNull()
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public List<Category> nested() {
       return this.categoryRepository.nested(SecurityContextUtils.CompanyIds()).orElseThrow(() -> new RuntimeException("Categories not found"));
    }

    @Override
    public Category store(Category category) {
        return this.categoryRepository.save(category);
    }
}
