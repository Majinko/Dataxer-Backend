package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.enums.CategoryType;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private List<Category> categories = new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public List<Category> all() {
        return this.categoryRepository.findAllByCompanyIdInOrderByPositionAsc(SecurityUtils.companyIds());
    }

    @Override
    public Category storeOrUpdate(Category category) {
        if (category.getId() != null) {
            return this.updateCategory(category);
        }

        return this.categoryRepository.save(category);
    }

    @Override
    public void updateTree(List<Category> categories) {
        this.prepareTree(categories, null);

        this.categoryRepository.saveAll(this.categories);
    }

    @Override
    public void delete(Long id) {
        if (!this.categoryRepository.findAllByParentIdAndCompanyId(id, SecurityUtils.companyId()).isEmpty()) {
            throw new RuntimeException("Category has children :(");
        }

        this.categoryRepository.deleteById(id);
    }

    @Override
    public List<Category> allByType(String type) {
        return this.categoryRepository.findAllByType(CategoryType.valueOf(type), SecurityUtils.companyIds());
    }

    @Override
    public Category findById(Long id) {
        return this.categoryRepository
                .findByIdAndCompanyIdIn(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("category not found"));
    }

    // private methods
    private Category updateCategory(Category category) {
        return this.categoryRepository.findByIdAndCompanyIdIn(category.getId(), SecurityUtils.companyIds()).map(c -> {
            c.setName(category.getName());
            c.setParentId(category.getParentId());
            c.setCategoryType(category.getCategoryType());
            c.setCategoryGroup(category.getCategoryGroup());

            return this.categoryRepository.save(c);
        }).orElse(null);
    }

    private void prepareTree(List<Category> categories, Long parentId) {
        categories.forEach(c -> {
            c.setParentId(parentId);

            this.categories.add(c);

            if (c.getChildren() != null) {
                this.prepareTree(c.getChildren(), c.getId());
            }
        });
    }
}
