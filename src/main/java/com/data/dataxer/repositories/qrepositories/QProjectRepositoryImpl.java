package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.domain.QProject;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QProjectRepositoryImpl implements QProjectRepository {
    private final JPAQueryFactory query;

    public QProjectRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    private Long total() {
        QProject qProject = QProject.project;

        return query.selectFrom(qProject).fetchCount();
    }

    @Override
    public Page<Project> paginate(Pageable pageable, List<Long> companyIds) {
        QProject qProject = QProject.project;

        List<Project> projectList = query
                .selectFrom(qProject)
                .leftJoin(qProject.contact).fetchJoin()
                .fetch();

        return new PageImpl<>(projectList, pageable, total());
    }

    @Override
    public Project getById(Long id, List<Long> companyIds) {
        QProject qProject = QProject.project;

        return query
                .selectFrom(qProject)
                .where(qProject.company.id.in(companyIds))
                .where(qProject.id.eq(id))
                .join(qProject.contact)
                .fetchJoin()
                .fetchOne();

    }
}
