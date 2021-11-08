package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.enums.CategoryType;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;


    @Override
    public List<Category> all() {
        return this.categoryRepository.findAllByCompanyIdInOrderByPositionAsc(SecurityUtils.companyIds());
    }

    @Override
    public Category store(Category category) {
        return this.categoryRepository.save(category);
    }

    @Override
    public void updateTree(List<Category> categories) {
        this.categoryRepository.saveAll(categories);
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
}
