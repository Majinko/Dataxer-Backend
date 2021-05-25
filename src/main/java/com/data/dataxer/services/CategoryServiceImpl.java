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
    @Transactional
    public void updateTree(List<Category> categories, Category category) {
        if (!categories.isEmpty()) {
            if (category == null) {
                category = this.qCategoryRepository.getBaseRoot(SecurityUtils.companyId()).orElse(null);
            }
            for (int i = 0; i <= categories.size() - 1; i++) {
                Category processedCategory = categories.get(i);
                List<Category> children = processedCategory.getChildren();
                //need little change for FE => moved category has just id and name, missing lft, rgt
                if (processedCategory.getParent() == null || processedCategory.getLft() == null || processedCategory.getRgt() == null || processedCategory.getPosition() == null) {
                    processedCategory = this.qCategoryRepository.getById(processedCategory.getId(), SecurityUtils.companyId());
                }
                if (processedCategory.getPosition() != i) {
                    this.qCategoryRepository.updateCategoryPosition(processedCategory.getId(), i, SecurityUtils.companyId());
                }
                if (processedCategory.getParent().getId() != category.getId()) {
                    List<Category> categoriesToReAdd = removeFromOldParent(processedCategory);
                    this.addChild(category, processedCategory, (long) i);
                    categoriesToReAdd.forEach(category1 -> this.addChild(category1.getParent(), category1, null));
                }
                this.updateTree(children, processedCategory);
            }
        }
    }

    @Override
    @Transactional
    public Category store(Category category) {
        //if parent is null parent is baseRoot
        if (category.getParent() == null) {
            Category baseRoot = this.qCategoryRepository.getBaseRoot(SecurityUtils.companyId()).orElse(null);
            if ( baseRoot == null ) {
                baseRoot = this.createBaseRoot();
            }
            return this.addChild(baseRoot, category, null);
        } else {
            Category parent = this.qCategoryRepository.getById(category.getParent().getId(), SecurityUtils.companyId());
            return this.addChild(parent, category, null);
        }
    }

    @Override
    public List<Category> all() {
        return categoryRepository
                .findAllByDepthGreaterThanAndCompanyIdInOrderByDepthAscPositionAsc(-1, SecurityUtils.companyIds())
                .orElse(new ArrayList<>());
    }

    @Override
    public List<Category> nested() {
        Category baseRoot = this.qCategoryRepository.getBaseRoot(SecurityUtils.companyId()).orElse(null);
        return categoryRepository.findAllByDepthGreaterThanAndCompanyIdInAndParentIsOrderByDepthAscPositionAsc(-1, SecurityUtils.companyIds(), baseRoot).orElse(new ArrayList<>());
    }
    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findByIdAndCompanyIdIn(id, SecurityUtils.companyIds());
        removeChild(category);
    }

    private Category addChild(Category parent, Category child, Long forcedPosition) {
        Category childWithHighestRgt = this.qCategoryRepository.findChildWithHighestRgt(parent, SecurityUtils.companyId());
        Integer childLft;
        Long position = this.qCategoryRepository.getCountOfChildren(parent.getId(), SecurityUtils.companyId());
        if (childWithHighestRgt == null) {
            childLft = parent.getLft() + 1;
            this.qCategoryRepository.findByLftGreaterEqualThan(childLft, SecurityUtils.companyId()).forEach(
                    category -> category.setLft(category.getLft() + 2)
            );
            this.qCategoryRepository.findByRgtGreaterThan(parent.getLft(), SecurityUtils.companyId()).forEach(
                    category -> category.setRgt(category.getRgt() + 2)
            );
        } else {
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
        if (forcedPosition == null) {
            child.setPosition(position);
        } else {
            child.setPosition(forcedPosition);
        }
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
        decrementSiblingsPosition(category);
        removed.forEach(category1 -> this.categoryRepository.delete(category1));
    }

    private void decrementSiblingsPosition(Category category) {
        List<Category> siblings = this.qCategoryRepository.findSiblingsWithHigherPosition(category, SecurityUtils.companyId());
        siblings.forEach(sibling -> {
            sibling.setPosition(sibling.getPosition() - 1);
        });
        //this.categoryRepository.saveAll(siblings);
    }

    private Category createBaseRoot() {
        Category baseRoot = new Category();
        baseRoot.setName("BASE");
        baseRoot.setLft(1);
        baseRoot.setRgt(2);
        baseRoot.setDepth(-1);
        return this.categoryRepository.save(baseRoot);
    }

    private List<Category> removeFromOldParent(Category category) {
        List<Category> subTree = this.qCategoryRepository.findSubTree(category, SecurityUtils.companyId());
        subTree.remove(category);

        Integer decrement = category.getRgt() - category.getLft() + 1;

        List<Category> categoriesToUpdate = this.qCategoryRepository.findByLftGreaterThan(category.getRgt(), SecurityUtils.companyId());
        categoriesToUpdate.forEach(category1 -> category1.setLft(category1.getLft() - decrement)
        );
        this.categoryRepository.saveAll(categoriesToUpdate);

        categoriesToUpdate = this.qCategoryRepository.findByRgtGreaterThan(category.getRgt(), SecurityUtils.companyId());
        categoriesToUpdate.forEach(category1 -> category1.setRgt(category1.getRgt() - decrement)
        );
        this.categoryRepository.saveAll(categoriesToUpdate);

        return subTree;
    }

    //*********** Recreate categories after import ****************************

    @Override
    @Transactional
    public void recreateTree() {
        Category baseCategory = this.qCategoryRepository.getBaseRoot(SecurityUtils.companyId()).orElse(null);
        if (baseCategory != null) {
            return;
        }

        //reset lft rgt to null for all
        this.qCategoryRepository.updateOldCategoryLftAndRgtToNull(SecurityUtils.companyId());
        baseCategory = this.createBaseRoot();

        addAllCategoryChildren(baseCategory);
    }

    private void addAllCategoryChildren(Category processedCategory) {
        List<Category> oldCategoryChildren;
        if (processedCategory.getName().equals("BASE")) {
            oldCategoryChildren = this.qCategoryRepository.getAllOldRootCategories(SecurityUtils.companyId());
        } else {
            oldCategoryChildren = this.qCategoryRepository.getOldCategoryChildren(processedCategory, SecurityUtils.companyId());
        }
        for (Category category : oldCategoryChildren) {
            addChild(processedCategory, category, null);
            addAllCategoryChildren(category);
        }
    }

    //*************************************************************************

}
