package com.data.dataxer.qrepositores;

import com.data.dataxer.models.domain.Contact;

import com.querydsl.core.types.Predicate;

import java.util.List;

public interface QContactRepository {
    public List<Contact> filtering(Predicate predicate);
    
    public List<Contact> allWithProjects(List<Long> companyIds);

    public Contact getById(Long id);

    public Contact getByEmail(String email);

    public Contact getByName(String name);
}
