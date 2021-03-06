package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {
    Category findByIdAndCompanyIdIn(Long id, List<Long> companyIds);
    Optional<List<Category>> findAllByCompanyIdIn(List<Long> companyIds);

    /*@EntityGraph(attributePaths = "children")*/
    Optional<List<Category>> findAllByCompanyIdInAndParentIsNull(List<Long> companyIds);

    /**
     * Retrieves all nodes with no child nodes (i.e. leaf nodes).
     *
     * @return {@code Collection} of {@code Node} objects
     */
    @Query("SELECT c FROM Category c WHERE c.rgt = c.lft + 1")
    List<Category> findLeafNodes();


    /**
     * Retrieves the path for passed node.
     * <br/>
     * Path is a {@code List} object where first element is the root node
     * and the last element is the immediate parent of passed node.
     *
     * @param node a node to retrieve a path for
     * @return list of nodes or empty list if passed node was the root node or was not found
     */
    @Query("SELECT parent FROM Category category, Category parent " +
            "WHERE category.lft BETWEEN parent.lft AND parent.rgt " +
            "AND parent != ?1 " +
            "AND category = ?1 ORDER BY category.lft")
    List<Category> findPathForNode(Category node);


    /**
     * Retrieves a list that represents a flattened tree of child nodes for passed node.
     * <br/>
     * The first element in the returned {@code List} object is the left-most child of passed node
     * followed by the left-most child of immediate descendant, then followed by the second child
     * of passed node if there are no more child nodes of it's left-most child and so on.
     *
     * @param category a node to retrieve child nodes for
     * @return list of nodes or empty list if passed node was the root node or was not found
     */
    @Query("SELECT child FROM Category category, Category child " +
            "WHERE (category.lft BETWEEN category.lft AND category.rgt) " +
            "AND (category.rgt BETWEEN category.lft AND category.rgt) " +
            "AND child != ?1 " +
            "AND category = ?1 ORDER BY child.lft")
    List<Category> findChildNodesForNode(Category category);

    /**
     * Retrieves a depth of a node.
     *
     * @param category a node to retrieve depth for
     * @return depth of a node in the range of 0...n
     */
    @Query("SELECT count(parent) FROM Category category, Category parent " +
            "WHERE category.lft BETWEEN parent.rgt AND parent.rgt " +
            "AND parent != ?1 AND node = ?1")
    int findNodeDepth(Category category);
}
