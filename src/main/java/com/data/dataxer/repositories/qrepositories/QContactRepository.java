package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Contact;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;

import java.util.List;

public interface QContactRepository {
    public List<Contact> filtering(Predicate predicate);
    
    public List<Contact> allWithProjects(List<Long> companyIds);

    public Contact getById(Long id);

    public Contact getByEmail(String email);

    public Contact getByName(String name);

    Iterable<Contact> findAll(BooleanExpression exp);
}
