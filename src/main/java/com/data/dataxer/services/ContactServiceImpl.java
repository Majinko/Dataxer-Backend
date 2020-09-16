package com.data.dataxer.services;

import com.data.dataxer.models.domain.Contact;

import com.data.dataxer.repositories.ContactRepository;

import com.data.dataxer.repositories.qrepositories.QContactRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
    public List<Contact> findByFirstNameAndLastName(String firstName, String lastName) {
        return contactRepository
                .findFirst5ByFirstNameContainingAndLastNameContaining(firstName, lastName)
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
    public Contact update(Contact c, Long id) {
        contactRepository.findByIdAndCompanyIdIn(id, SecurityUtils.companyIds())
                .map(contact -> {

                    // do mapu mi pride vysledok metody findById cize kontakt
                    contact.setName(c.getName());
                    contact.setPhotoUrl(c.getPhotoUrl());
                    contact.setStreet(c.getStreet());
                    contact.setCity(c.getCity());
                    contact.setCountry(c.getCountry());
                    contact.setPostalCode(c.getPostalCode());
                    contact.setRegNumber(c.getRegNumber());
                    contact.setEmail(c.getEmail());
                    contact.setPhone(c.getPhone());
                    //contact.setCompany(SecurityUtils.loggedUser().getCompanies().get(0));

                    return contactRepository.save(contact);
                });

        return c;
    }

    @Override
    public void delete(Long id) {
        contactRepository.findById(id).map(contact -> {
            contact.setDeletedAt(LocalDateTime.now());

            return contactRepository.save(contact);
        });
    }
}
