package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.domain.QContact;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long>, QuerydslPredicateExecutor<Contact>, QuerydslBinderCustomizer<QContact> {
    Optional<Contact> findByIdAndCompanyIdIn(Long id, List<Long> companyIds);

    Optional<List<Contact>> findAllByCompanyIdIn(List<Long> companyIds);

    Optional<List<Contact>> findFirst5ByFirstNameContainingAndLastNameContaining(String firstName, String lastName);

    Optional<Page<Contact>> findAllByEmailContainingAndCompanyIdIn(Pageable pageable, String email, List<Long> companyIds);

    @Override
    default public void customize(QuerydslBindings bindings, QContact root) {
        bindings.bind(String.class).first((StringPath path, String value) -> path.containsIgnoreCase(value));
    }
}
