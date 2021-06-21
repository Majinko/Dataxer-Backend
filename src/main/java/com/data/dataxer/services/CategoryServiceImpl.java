package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private int position = 0;
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> all() {
        return this.categoryRepository.findAllByCompanyIdOrderByPositionAsc(SecurityUtils.companyId());
    }

    @Override
    public Category store(Category category) {
        return this.categoryRepository.save(category);
    }

    @Override
    public void updateTree(List<Category> categories, Category category) {
        if (!categories.isEmpty()) {
            categories.forEach(c -> {
                if (c != null) {
                    Category cc = categoryRepository.findByIdAndCompanyId(c.getId(), SecurityUtils.companyId()).orElseThrow(() -> new RuntimeException("Category not found"));

                    cc.setPosition(this.position++);
                    cc.setParentId(category == null ? null : category.getId());

                    categoryRepository.save(cc);

                    if (!c.getChildren().isEmpty()) {
                        this.updateTree(c.getChildren(), c);
                    }
                }
            });
        }
    }

    @Override
    public void delete(Long id) {
        if (!this.categoryRepository.findAllByParentIdAndCompanyId(id, SecurityUtils.companyId()).isEmpty()) {
           throw new RuntimeException("Category has children :(");
        }

        this.categoryRepository.deleteById(id);
    }
}
