package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.QCategory;
import com.data.dataxer.models.domain.QTime;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QCategoryRepositoryImpl implements QCategoryRepository {
    private final JPAQueryFactory query;

    public QCategoryRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Category> allUserCategoryByTime(String uid, Long appProfileId) {
        return query.selectFrom(QCategory.category)
                .where(QCategory.category.id.in(
                        JPAExpressions
                                .select(QTime.time1.category.id)
                                .from(QTime.time1)
                                .where(QTime.time1.user.uid.eq(uid))
                                .fetchAll()
                )).fetch();
    }
}
