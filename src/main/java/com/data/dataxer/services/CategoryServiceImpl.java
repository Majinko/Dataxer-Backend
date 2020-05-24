package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> all() {
        return categoryRepository
                .findAllByCompanyIdIn(SecurityUtils.companyIds())
                .orElse(null);
    }

    @Override
    public List<Category> nested() {
        return categoryRepository
                .findAllByCompanyIdInAndParentIsNull(SecurityUtils.companyIds()).orElse(null);
    }

    @Override
    public Category store(Category category) {
        return this.categoryRepository.save(category);
    }

    @Override
    public void updateTree(List<Category> categories, Category category) {
        if (!categories.isEmpty()) {
            categories.forEach(c -> {
                Category cc = categoryRepository.findByIdAndCompanyIdIn(c.getId(), SecurityUtils.companyIds());

                cc.setParent(category);
                categoryRepository.save(cc);

                if (!c.getChildren().isEmpty()) {
                    this.updateTree(c.getChildren(), c);
                }
            });
        }
    }

    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findByIdAndCompanyIdIn(id, SecurityUtils.companyIds());
        categoryRepository.delete(category);
    }
}
