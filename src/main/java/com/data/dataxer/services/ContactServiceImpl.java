package com.data.dataxer.services;

import com.data.dataxer.models.domain.Contact;

import com.data.dataxer.models.domain.QContact;
import com.data.dataxer.repositories.ContactRepository;
import com.data.dataxer.repositories.Predicates.CustomPredicatesBuilder;
import com.data.dataxer.repositories.qrepositories.QContactRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.github.vineey.rql.filter.parser.DefaultFilterParser;
import com.github.vineey.rql.querydsl.select.QuerydslSelectContext;
import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.github.vineey.rql.querydsl.filter.QueryDslFilterContext.withMapping;

@Service
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;
    private final QContactRepository qContactRepository;

    public ContactServiceImpl(ContactRepository contactRepository, QContactRepository qContactRepository) {
        this.contactRepository = contactRepository;
        this.qContactRepository = qContactRepository;
    }

    @Override
    public List<Contact> filtering(Predicate predicate) {
        return this.qContactRepository.filtering(predicate);
    }

    @Override
    public List<Contact> findAll() {
        return contactRepository.findAllByCompanyIdIn(SecurityUtils.companyIds()).orElse(null);
    }

    @Override
    public List<Contact> findByName(String name) {
        return contactRepository
                .findFirst5ByNameContaining(name)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public Page<Contact> paginate(Pageable pageable, String email) {
        return contactRepository.findAllByEmailContainingAndCompanyIdIn(pageable, email, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public Contact getById(Long id) {
        return contactRepository.findById(id).orElse(null);
    }

    @Override
    public Contact store(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public Contact update(Contact c) {
        this.contactRepository.save(c);

        return c;
    }

    @Override
    public void delete(Long id) {
        contactRepository.delete(this.getById(id));
    }

    @Override
    public Iterable<Contact> filteringV2(String search) {
        DefaultFilterParser filterParser = new DefaultFilterParser();

        String rqlFilter = "(contact.name == 'os*') and (contact.company.name == 'dataid*')";

        Map<String, Path> pathHashMap = ImmutableMap.<String, Path>builder()
                .put("contact.name", QContact.contact.name)
                .put("contact.company.name", QContact.contact.company.name)
                .put("contact.id", QContact.contact.id)
                .build();

        Predicate predicate = filterParser.parse(rqlFilter, withMapping(pathHashMap));

        List<Contact> contacts = this.qContactRepository.filtering(predicate);

        CustomPredicatesBuilder<Contact> builder = new CustomPredicatesBuilder<>("contact");
        return qContactRepository.findAll(builder.parsePattern(search));
    }

    @Override
    public List<Contact> getContactByIds(List<Long> contactIds) {
        return this.qContactRepository.getAllByIds(contactIds, SecurityUtils.companyIds());
    }
}
