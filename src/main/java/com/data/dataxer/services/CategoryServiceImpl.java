package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;
import works.hacker.mptt.TreeRepository;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryRepository.setEntityClass(Category.class);
    }

    @Override
    public Category store(Category category) {
        try {
            if (category.getParent() == null) {
                Long id = this.categoryRepository.startTree(category);
                return this.getById(id);
            } else {
                this.categoryRepository.addChild(category.getParent(), category);
            }
        } catch (TreeRepository.NodeAlreadyAttachedToTree | TreeRepository.NodeNotInTree exception) {
            throw new RuntimeException("Category save failed. Reason: " + exception.getMessage());
        }
        return  this.categoryRepository.save(category);
    }

    @Override
    public Category getById(Long id) {
        return this.categoryRepository.findCategoryByIdAndCompanyId(id, SecurityUtils.companyId());
    }

    @Override
    public List<Category> all() {
        return this.categoryRepository.findAllByCompanyId(SecurityUtils.companyId());
    }

    @Override
    public List<Category> nested() {
        return this.categoryRepository.findAllByCompanyIdAndParentIsNull(SecurityUtils.companyId());
    }

    @Override
    public void delete(Long id) {
        List<Category> categoriesToDelete = this.categoryRepository.findSubTree(this.getById(id));
        this.categoryRepository.deleteInBatch(categoriesToDelete);
    }
}
