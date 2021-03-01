package com.data.dataxer.services;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.dto.CategoryNestedDTO;
import com.data.dataxer.repositories.CategoryRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import works.hacker.mptt.TreeRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
        this.categoryRepository.setEntityClass(Category.class);
    }

    @Override
    @Transactional
    public void store(Long parentId, String name) {
        try {
            Category parent = this.getById(parentId);
            Category node = new Category(name);
            if (parent == null) {
                System.out.println("Parent is null");
                Category treeRoot = this.categoryRepository.findCompanyRoot(SecurityUtils.companyId()).orElse(null);
                if (treeRoot == null) {
                    Long treeId = this.categoryRepository.startTree(new Category(""));
                    treeRoot = this.categoryRepository.findTreeRoot(treeId);
                }
                node.setPosition(this.getNextPosition(treeRoot));
                this.categoryRepository.addChild(treeRoot, node);
            } else {
                node.setPosition(this.getNextPosition(parent));
                this.categoryRepository.addChild(parent, node);
            }
        } catch (TreeRepository.NodeAlreadyAttachedToTree nodeAlreadyAttachedToTree) {
            nodeAlreadyAttachedToTree.printStackTrace();
        } catch (TreeRepository.NodeNotInTree nodeNotInTree) {
            nodeNotInTree.printStackTrace();
        }
    }

    @Override
    public Category getById(Long id) {
        return this.categoryRepository.findCategoryByIdAndCompanyId(id, SecurityUtils.companyId())
                .orElse(null);
    }

    @Override
    public List<Category> all() {
        return this.categoryRepository.findAllByCompanyId(SecurityUtils.companyId());
    }

    @Override
    public List<CategoryNestedDTO> nested(List<Category> categories) {
        if (categories == null) {
            Category root = this.categoryRepository.findCompanyRoot(SecurityUtils.companyId())
                    .orElseThrow(() -> new RuntimeException("Missing or too many company roots"));
            categories = new ArrayList<>();
            categories.addAll(this.categoryRepository.findChildren(root));
        } else if (categories.isEmpty()){
            return new ArrayList<>();
        }
        List<CategoryNestedDTO> response = new ArrayList<>();
        categories.forEach(category -> {
            CategoryNestedDTO categoryNestedDTO = new CategoryNestedDTO();
            categoryNestedDTO.setId(category.getId());
            categoryNestedDTO.setName(category.getName());
            categoryNestedDTO.setChildren(this.nested(this.categoryRepository.findChildren(category)));
            response.add(categoryNestedDTO);
        });

        return response;
    }

    @Override
    public List<Category> getChildren(Long parentId) {
        return this.categoryRepository.findChildren(this.getById(parentId)).stream()
                .sorted(Comparator.comparingInt(Category::getPosition)).collect(Collectors.toList());
    }

    @Override
    public void updateTree(Long parentCategoryId, Category category, Long categoryId) {
        if (parentCategoryId == null || parentCategoryId <= 0) {
            List<Category> subTree = this.categoryRepository.findSubTree(category);
            //revert list so first element is new root
            Collections.reverse(subTree);
            //find all roots to change positions
            List<Category> categoriesToChangePosition = this.categoryRepository.findAllByCompanyIdAndDepthOrderByPositionAsc(SecurityUtils.companyId()).stream()
                    .filter(c -> c.getPosition() >= category.getPosition()).collect(Collectors.toList());
            this.changeCategoryPositions(categoriesToChangePosition, 1);
            this.moveToNewParent(subTree, null, true);
        } else {
            Category parent = this.getById(parentCategoryId);
            //load children of parent
            List<Category> children = this.categoryRepository.findChildren(parent);

            boolean positionChangeOnly = children.stream().filter(c-> c.getId() == categoryId).count() == 1;

            if (positionChangeOnly) {
                Category oldCategory = children.stream().filter(c -> c.getId() == categoryId).findFirst()
                        .orElseThrow(() -> new RuntimeException("Cannot find category with old position"));
                //we are changing position so we need change all between old and new position
                if (category.getPosition() < oldCategory.getPosition()) {
                    List<Category> categoriesToChangePosition = children.stream()
                            .filter(c -> c.getPosition() >= category.getPosition() && c.getPosition() < oldCategory.getPosition()).collect(Collectors.toList());
                    this.changeCategoryPositions(categoriesToChangePosition, 1);
                } else {
                    List<Category> categoriesToChangePosition = children.stream()
                            .filter(c -> c.getPosition() <= category.getPosition() && c.getPosition() > oldCategory.getPosition()).collect(Collectors.toList());
                    this.changeCategoryPositions(categoriesToChangePosition, -1);
                }
                //update position for moved category
                this.categoryRepository.setCategoryPosition(categoryId, category.getPosition(), SecurityUtils.companyId());
            } else {
                //we are adding new children so we need just move all with position >= + 1
                List<Category> categoriesToChangePosition = children.stream()
                        .filter(c -> c.getPosition() >= category.getPosition()).collect(Collectors.toList());
                this.changeCategoryPositions(categoriesToChangePosition, 1);
                //old parent children position change > than
                List<Category> subTree = this.categoryRepository.findSubTree(category);
                //revert list so first element is category
                Collections.reverse(subTree);
                //find all old siblings to change positions
                List<Category> oldParentChildrenToChangePositions = this.categoryRepository.findChildren(
                        this.categoryRepository.findParent(category)
                                .orElseThrow(() -> new RuntimeException("Old parent not found")))
                        .stream().filter(c -> c.getPosition() >= subTree.get(0).getPosition()).collect(Collectors.toList());
                //change position for all needed
                this.changeCategoryPositions(oldParentChildrenToChangePositions, -1);
                this.moveToNewParent(subTree, parent,false);
            }
        }
    }

    @Override
    public void delete(Long id) {
        List<Category> categoriesToDelete = this.categoryRepository.findSubTree(this.getById(id));
        this.categoryRepository.deleteInBatch(categoriesToDelete);
    }

    private Integer getNextPosition(Category parent) {
        Integer position = -1;
        List<Category> categories = this.categoryRepository.findChildren(parent);

        for (Category category:categories) {
            if (category.getPosition() > position) {
                position = category.getPosition();
            }
        }

        return position + 1;
    }

    private void moveToNewParent(List<Category> subTree, Category newParent, boolean makeNewRoot) {
        HashMap<Category, Category> mappedCategories = mapNewCategoryToOld(subTree);
        try {
            if (makeNewRoot) {
                //create new root
                this.categoryRepository.startTree(mappedCategories.get(subTree.get(0)));
            } else {
                System.out.println("New parent name: " + newParent.getName() + " and child name: " + mappedCategories.get(subTree.get(0)).getName());
                this.categoryRepository.addChild(newParent, mappedCategories.get(subTree.get(0)));
            }
            subTree.forEach(category -> {
                List<Category> children = this.categoryRepository.findChildren(category);
                children.forEach(child -> {
                    try {
                        this.categoryRepository.addChild(mappedCategories.get(category), mappedCategories.get(child));
                    } catch (TreeRepository.NodeAlreadyAttachedToTree nodeAlreadyAttachedToTree) {
                        throw new RuntimeException("Node with name: " + child.getName() + " is already attached to tree");
                    } catch (TreeRepository.NodeNotInTree nodeNotInTree) {
                        throw new RuntimeException("Parent node with name: " + mappedCategories.get(category).getName() + ", " + mappedCategories.get(category).getPosition() + " is not in tree");
                    }
                });
            });
            //delete old nodes
            this.categoryRepository.deleteInBatch(subTree);
        } catch (TreeRepository.NodeAlreadyAttachedToTree nodeAlreadyAttachedToTree) {
            throw new RuntimeException("New root is already attached, cannot create new tree root");
        } catch (TreeRepository.NodeNotInTree nodeNotInTree) {
            throw new RuntimeException("Parent node with name: " + newParent.getName() + " is not in tree (parent)");
        }
    }

    private HashMap<Category, Category> mapNewCategoryToOld(List<Category> oldCategories) {
        HashMap<Category, Category> mappedCategories = new HashMap<>();

        oldCategories.forEach(category -> mappedCategories.put(category, this.createNewFromOld(category)));

        return mappedCategories;
    }

    private Category createNewFromOld(Category oldCategory) {
        Category newCategory = new Category();
        newCategory.setName(oldCategory.getName());
        newCategory.setPosition(oldCategory.getPosition());

        return newCategory;
    }

    private void changeCategoryPositions(List<Category> categoriesToChangePosition, int value) {
        List<Long> categoryIds = categoriesToChangePosition.stream().map(Category::getId).collect(Collectors.toList());
        this.categoryRepository.updateCategoriesPosition(categoryIds, value, SecurityUtils.companyId());
    }
}
