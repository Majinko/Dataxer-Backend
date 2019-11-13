package com.data.dataxer.services;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.dto.ContactDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ContactService {
    List<Contact> findAll();

    List<Contact> findByFirstNameAndLastName(String firstName, String lastName);

    Page<Contact> paginate(Pageable pageable);

    Page<Contact> paginateFilter(Pageable pageable, String firstName);

    Contact getById(Long id);

    Contact store(Contact contactDTO);

    Contact update(Contact contactDTO, Long id);

    void delete(Long id);
}
