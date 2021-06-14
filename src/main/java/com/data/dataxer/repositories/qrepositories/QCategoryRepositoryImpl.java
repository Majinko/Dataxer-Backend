package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.QCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class QCategoryRepositoryImpl implements QCategoryRepository {

    private final JPAQueryFactory query;

    public QCategoryRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }


    @Override
    public Optional<Category> getBaseRoot(Long companyId) {
        return Optional.ofNullable(this.query.selectFrom(QCategory.category)
                .where(QCategory.category.parent.isNull())
                .where(QCategory.category.depth.eq(-1))
                .where(QCategory.category.company.id.eq(companyId))
                .fetchFirst()
        );
    }

    @Override
    public Category findChildWithHighestRgt(Category parent, Long companyId) {
        return this.query.selectFrom(QCategory.category)
                .where(QCategory.category.parent.id.eq(parent.getId()))
                .where(QCategory.category.depth.eq(parent.getDepth() + 1))
                .where(QCategory.category.company.id.eq(companyId))
                .orderBy(QCategory.category.rgt.desc())
                .fetchFirst();
    }

    @Override
    public Optional<Category> getById(Long id, Long companyId) {
        return Optional.ofNullable(this.query.selectFrom(QCategory.category)
                .where(QCategory.category.id.eq(id))
                .where(QCategory.category.company.id.eq(companyId))
                .fetchOne());
    }

    @Override
    public List<Category> findByLftGreaterEqualThan(Integer lft, Long companyId) {
        return this.query.selectFrom(QCategory.category)
                .where(QCategory.category.lft.goe(lft))
                .where(QCategory.category.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public List<Category> findByLftGreaterThan(Integer lft, Long companyId) {
        return this.query.selectFrom(QCategory.category)
                .where(QCategory.category.lft.gt(lft))
                .where(QCategory.category.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public List<Category> findByRgtGreaterThan(Integer rgt, Long companyId) {
        return this.query.selectFrom(QCategory.category)
                .where(QCategory.category.rgt.gt(rgt))
                .where(QCategory.category.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public List<Category> findSubTree(Category category, Long companyId) {
        return this.query.selectFrom(QCategory.category)
                .where(QCategory.category.lft.goe(category.getLft()))
                .where(QCategory.category.rgt.loe(category.getRgt()))
                .where(QCategory.category.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public List<Category> findSiblingsWithHigherPosition(Category category, Long companyId) {
        return this.query.selectFrom(QCategory.category)
                .where(QCategory.category.parent.id.eq(category.getId()))
                .where(QCategory.category.position.gt(category.getPosition()))
                .where(QCategory.category.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public long getCountOfChildren(Long parentId, Long companyId) {
        return this.query.selectFrom(QCategory.category)
                .where(QCategory.category.parent.id.eq(parentId))
                .where(QCategory.category.company.id.eq(companyId))
                .fetchCount();
    }

    @Override
    public void updateCategoryPosition(Long id, long position, Long companyId) {
        this.query.update(QCategory.category)
                .set(QCategory.category.position, position)
                .where(QCategory.category.id.eq(id))
                .where(QCategory.category.company.id.eq(companyId))
                .execute();
    }

    @Override
    public Optional<List<Category>> getAllRootCategories(Long companyId) {
        return Optional.ofNullable(this.query.selectFrom(QCategory.category)
                .where(QCategory.category.depth.eq(0))
                .where(QCategory.category.company.id.eq(companyId))
                .fetch());
    }

    @Override
    public Optional<List<Category>> getChildren(Long parentId, Long companyId) {
        return Optional.ofNullable(this.query.selectFrom(QCategory.category)
                .where(QCategory.category.parent.id.eq(parentId))
                .where(QCategory.category.company.id.eq(companyId))
                .fetch());
    }

    //***************************************************
    // Queries for recreating category tree

    //***************************************************

    @Override
    public List<Category> getAllOldRootCategories(Long companyId) {
        return this.query.selectFrom(QCategory.category)
                .where(QCategory.category.parent.isNull())
                .where(QCategory.category.depth.isNull())
                .where(QCategory.category.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public List<Category> getOldCategoryChildren(Category processedCategory, Long companyId) {
        return this.query.selectFrom(QCategory.category)
                .where(QCategory.category.parent.id.eq(processedCategory.getId()))
                .where(QCategory.category.company.id.eq(companyId))
                .fetch();
    }

    @Override
    public void updateOldCategoryLftAndRgtToNull(Long companyId) {
        this.query.update(QCategory.category)
                .setNull(QCategory.category.lft)
                .setNull(QCategory.category.rgt)
                .where(QCategory.category.company.id.eq(companyId))
                .execute();
    }

}
