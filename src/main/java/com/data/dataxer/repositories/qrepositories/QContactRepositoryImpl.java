package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.domain.QContact;
import com.data.dataxer.models.domain.QProject;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QContactRepositoryImpl implements QContactRepository {
    private final JPAQueryFactory query;

    private QContact CONTACT = QContact.contact;

    public QContactRepositoryImpl(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    public List<Contact> filtering(Predicate predicate) {
        return query.selectFrom(CONTACT).where(predicate).fetch();
    }

    @Override
    public List<Contact> allWithProjects(List<Long> companyIds) {
        QProject PROJECT = QProject.project;

        return query
                .selectFrom(CONTACT)
                .where(CONTACT.company.id.in(companyIds))
                .join(CONTACT.projects, PROJECT)
                .fetch();
    }

    @Override
    public Contact getById(Long id) {
        return null;
    }

    @Override
    public Contact getByEmail(String email) {
        return null;
    }

    @Override
    public Contact getByName(String name) {
        return null;
    }
}
