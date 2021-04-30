package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.repositories.qrepositories.QCategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final QCategoryRepository qCategoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository, QCategoryRepository qCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.qCategoryRepository = qCategoryRepository;
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



    //***********************************************************************
    //new implementation
    @Override
    @Transactional
    public Category store(Category category) {
        //if parent is null parent is baseRoot
        if (category.getParent() == null) {
            Category baseRoot = this.qCategoryRepository.getBaseRoot(SecurityUtils.companyId());
            if ( baseRoot == null ) {
                baseRoot = this.createBaseRoot();
            }
            return this.addChild(baseRoot, category);
        } else {
            Category parent = this.qCategoryRepository.getById(category.getParent().getId(), SecurityUtils.companyId());
            return this.addChild(parent, category);
        }
    }

    @Override
    public List<Category> all() {
        return categoryRepository
                .findAllByDepthGreaterThanAndCompanyIdInOrderByDepthAscLftAsc(-1, SecurityUtils.companyIds())
                .orElse(new ArrayList<>());
    }
    @Override
    public List<Category> nested() {
        Category baseRoot = this.qCategoryRepository.getBaseRoot(SecurityUtils.companyId());
        return categoryRepository.findAllByDepthGreaterThanAndCompanyIdInAndParentIsOrderByDepthAscLftAsc(-1, SecurityUtils.companyIds(), baseRoot).orElse(new ArrayList<>());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findByIdAndCompanyIdIn(id, SecurityUtils.companyIds());
        removeChild(category);
    }

    private Category addChild(Category parent, Category child) {
        Category childWithHighestRgt = this.qCategoryRepository.findChildWithHighestRgt(parent, SecurityUtils.companyId());
        Integer childLft;
        if (childWithHighestRgt == null) {
            childLft = parent.getLft() + 1;
            this.qCategoryRepository.findByLftGreaterEqualThan(childLft, SecurityUtils.companyId()).forEach(
                    category -> category.setLft(category.getLft() + 2)
            );
            this.qCategoryRepository.findByRgtGreaterThan(parent.getLft(), SecurityUtils.companyId()).forEach(
                    category -> category.setRgt(category.getRgt() + 2)
            );
        } else {
            System.out.println("Name =>" + childWithHighestRgt.getName());
            childLft = childWithHighestRgt.getRgt() + 1;
            this.qCategoryRepository.findByLftGreaterThan(childWithHighestRgt.getRgt(), SecurityUtils.companyId()).forEach(
                    category -> category.setLft(category.getLft() + 2)
            );
            this.qCategoryRepository.findByRgtGreaterThan(childWithHighestRgt.getRgt(), SecurityUtils.companyId()).forEach(
                    category -> category.setRgt(category.getRgt() + 2)
            );
        }
        Integer childRgt = childLft + 1;
        child.setLft(childLft);
        child.setRgt(childRgt);
        child.setDepth(parent.getDepth() + 1);
        child.setParent(parent);
        return this.categoryRepository.save(child);
    }

    private void removeChild(Category category) {
        List<Category> removed = this.qCategoryRepository.findSubTree(category, SecurityUtils.companyId());
        Integer decrement = category.getRgt() - category.getLft() + 1;
        this.qCategoryRepository.findByLftGreaterThan(category.getRgt(), SecurityUtils.companyId()).forEach(
                category1 -> category1.setLft(category1.getLft() - decrement)
        );
        this.qCategoryRepository.findByRgtGreaterThan(category.getRgt(), SecurityUtils.companyId()).forEach(
                category1 -> category1.setRgt(category1.getRgt() - decrement)
        );
        //this.categoryRepository.delete(category);
        removed.forEach(category1 -> this.categoryRepository.delete(category1));
    }

    private Category createBaseRoot() {
        Category baseRoot = new Category();
        baseRoot.setName("BASE");
        baseRoot.setLft(1);
        baseRoot.setRgt(2);
        baseRoot.setDepth(-1);
        return this.categoryRepository.save(baseRoot);
    }
}
