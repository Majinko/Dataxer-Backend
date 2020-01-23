package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityContextUtils;
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
                .findAllByCompanyIdInAndDeletedAtIsNull(SecurityContextUtils.CompanyIds())
                .orElse(null);
    }

    @Override
    public List<Category> nested() {
        return this.categoryRepository
                .findAllByCompanyIdInAndDeletedAtIsNullAndParentIsNull(SecurityContextUtils.CompanyIds())
                .orElseThrow(() -> new RuntimeException("Categories not found"));
    }

    @Override
    public Category store(Category category) {
        return this.categoryRepository.save(category);
    }

    @Override
    public void updateTree(List<Category> categories, Category category) {
        if (!categories.isEmpty()) {
            categories.forEach(c -> {
                Category cc = categoryRepository.findByIdAndCompanyIdIn(c.getId(), SecurityContextUtils.CompanyIds());

                cc.setParent(category);
                categoryRepository.save(cc);

                if (!c.getChildren().isEmpty()) {
                    this.updateTree(c.getChildren(), c);
                }
            });
        }
    }
}
