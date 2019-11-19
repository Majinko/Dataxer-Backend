package com.data.dataxer.qrepositores;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.domain.QContact;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class QContactRepository {
    private final JPAQueryFactory query;

    private QContact CONTACT = QContact.contact;

    public QContactRepository(EntityManager entityManager) {
        this.query = new JPAQueryFactory(entityManager);
    }

    public List<Contact> filtering(Predicate predicate){
        return query.selectFrom(CONTACT).where(predicate).fetch();
    }
}
