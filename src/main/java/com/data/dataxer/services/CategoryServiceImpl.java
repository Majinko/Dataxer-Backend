package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.enums.CategoryGroup;
import com.data.dataxer.models.enums.CategoryType;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CategoryServiceImpl implements CategoryService {
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
        List<Category> updateCategories = new ArrayList<>();

        this.prepareTree(categories, null, updateCategories);

        this.categoryRepository.saveAll(updateCategories);
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
    public List<Category> allByTypes(List<String> types) {
        List<CategoryType> categoryTypes = new ArrayList<>();

        types.forEach(cType -> {
            categoryTypes.add(CategoryType.valueOf(cType));
        });

        return this.categoryRepository.findAllByCategoryTypeInAndCompanyIdIn(categoryTypes, SecurityUtils.companyIds());
    }

    @Override
    public List<Category> allByGroups(List<String> groups) {
        List<CategoryGroup> categoryGroups = new ArrayList<>();

        groups.forEach(group -> {
            categoryGroups.add(CategoryGroup.valueOf(group));
        });

        return this.categoryRepository.findAllByCategoryGroupInAndCompanyIdInOrderByPosition(categoryGroups, SecurityUtils.companyIds());
    }

    @Override
    public Category findById(Long id) {
        return this.categoryRepository
                .findByIdAndCompanyIdIn(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("category not found"));
    }

    @Override
    public List<Category> allByGroupFromParent(String group) {
        Set<Long> categoriesId = new HashSet<>();

        // todo make better query in foreach is bad idea
        this.categoryRepository // fill categoriesId
                .findAllByCategoryGroupAndCompanyIdInAndParentIdIsNullOrderByPosition(CategoryGroup.valueOf(group), SecurityUtils.companyIds())
                .forEach(c -> categoriesId.addAll(this.categoryRepository.findAllChildIds(c.getId(), SecurityUtils.companyIds())));

        return this.categoryRepository.findAllByIdInAndCompanyIdInOrderByPosition(new ArrayList<>(categoriesId), SecurityUtils.companyIds());
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

    private void prepareTree(List<Category> categories, Long parentId, List<Category> updateCategories) {
        categories.forEach(c -> {
            c.setParentId(parentId);

            updateCategories.add(c);

            if (c.getChildren() != null) {
                this.prepareTree(c.getChildren(), c.getId(), updateCategories);
            }
        });
    }
}
