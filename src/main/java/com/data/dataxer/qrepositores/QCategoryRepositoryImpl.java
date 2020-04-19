package com.data.dataxer.qrepositores;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.QCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import javax.persistence.EntityManager;

@Repository
public class QCategoryRepositoryImpl implements QCategoryRepository {
    private final JPAQueryFactory query;
    private QCategory CATEGORY = QCategory.category;

    public QCategoryRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Category> findAllByCompanyIdIn(List<Long> companyIds) {
        return query.selectFrom(CATEGORY).where(CATEGORY.company.id.in(companyIds)).fetch();
    }
}
