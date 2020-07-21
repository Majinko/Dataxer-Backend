package com.data.dataxer.repositories.qrepositories;
import com.data.dataxer.models.domain.QTask;
import com.data.dataxer.models.domain.Task;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QTaskRepositoryImpl implements QTaskRepository {
    private final JPAQueryFactory query;

    public QTaskRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    private Long total() {
        QTask qTask = QTask.task;

        return query.selectFrom(qTask).fetchCount();
    }

    @Override
    public Task getById(Long id, List<Long> companyIds) {
        QTask qTask = QTask.task;

        return query
                .selectFrom(qTask)
                .where(qTask.company.id.in(companyIds))
                .where(qTask.id.eq(id))
                .join(qTask.user).fetchJoin()
                .join(qTask.project).fetchJoin()
                .join(qTask.category).fetchJoin()
                .fetchOne();
    }


    @Override
    public Page<Task> paginate(Pageable pageable, List<Long> companyIds) {
        QTask qTask = QTask.task;

        List<Task> tasks = query
                .selectFrom(qTask)
                .join(qTask.user).fetchJoin()
                .join(qTask.project).fetchJoin()
                .join(qTask.category).fetchJoin().fetch();

        return new PageImpl<>(tasks, pageable, total());
    }
}