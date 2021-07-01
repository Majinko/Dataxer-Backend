package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private int position = 0;

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public List<Category> all() {
        return this.categoryRepository.findAllByCompanyIdOrderByPositionAsc(SecurityUtils.companyId());
    }

    @Override
    public Category store(Category category) {
        return this.categoryRepository.save(category);
    }

    @Override
    public void updateTree(List<Category> categories) {
        List<Category> updateCategories = new ArrayList<>();

        this.prepareTree(categories, updateCategories, null);

        this.categoryRepository.saveAll(updateCategories);
    }

    public void prepareTree(List<Category> categories, List<Category> updateCategories, Category category) {
        if (!categories.isEmpty()) {
            categories.forEach(c -> {
                if (c != null) {
                    c.setPosition(this.position++);
                    c.setParentId(category == null ? null : category.getId());

                    updateCategories.add(c);

                    if (!c.getChildren().isEmpty()) {
                        this.prepareTree(c.getChildren(), updateCategories, c);
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
