package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<List<Contact>> findAllByDeletedAtIsNull();

    Optional<List<Contact>> findFirst5ByFirstNameContainingAndLastNameContainingAndDeletedAtIsNull(String firstName, String lastName);

    Optional<Page<Contact>> findAllByDeletedAtIsNull(Pageable pageable);

    Optional<Page<Contact>> findAllByDeletedAtIsNullAndFirstNameContaining(Pageable pageable, String firstName);
}
