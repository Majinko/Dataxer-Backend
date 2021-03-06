package com.data.dataxer.services;

import com.data.dataxer.models.domain.Contact;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService {
    Contact store(Contact contactDTO);

    Contact update(Contact contactDTO);

    Page<Contact> paginate(Pageable pageable, String rqlFilter, String sortExpression);

    Contact getById(Long id);

    void destroy(Long id);

    List<Contact> getContactByIds(List<Long> contactIds);

    List<Contact> findAll();

    List<Contact> findByName(String name);
}
