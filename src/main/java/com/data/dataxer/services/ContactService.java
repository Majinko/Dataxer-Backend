package com.data.dataxer.services;

import com.data.dataxer.models.domain.Contact;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService {
    List<Contact> filtering(Predicate predicate);

    List<Contact> findAll();

    List<Contact> findByFirstNameAndLastName(String firstName, String lastName);

    Page<Contact> paginate(Pageable pageable, String email);

    Contact getById(Long id);

    Contact store(Contact contactDTO);

    Contact update(Contact contactDTO, Long id);

    void delete(Long id);
}
