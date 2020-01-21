package com.data.dataxer.services;

import com.data.dataxer.models.domain.Contact;

import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.qrepositores.QContactRepository;
import com.data.dataxer.repositories.ContactRepository;

import com.data.dataxer.securityContextUtils.SecurityContextUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
        return contactRepository
                .findAllByDeletedAtIsNull()
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public List<Contact> findByFirstNameAndLastName(String firstName, String lastName) {
        return contactRepository
                .findFirst5ByFirstNameContainingAndLastNameContainingAndDeletedAtIsNull(firstName, lastName)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public Page<Contact> paginate(Pageable pageable, String email) {
        return contactRepository.findAllByDeletedAtIsNullAndEmailContainingAndAndCompanyIdIn(pageable, email, SecurityContextUtils.CompanyIds())
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
        contactRepository.findById(id)
                .map(contact -> {
                    // do mapu mi pride vysledok metody findById cize kontakt
                    contact.setFirstName(c.getFirstName());
                    contact.setLastName(c.getLastName());
                    contact.setPhotoUrl(c.getPhotoUrl());
                    contact.setStreet(c.getStreet());
                    contact.setCity(c.getCity());
                    contact.setCountry(c.getCountry());
                    contact.setPostalCode(c.getPostalCode());
                    contact.setRegNumber(c.getRegNumber());
                    contact.setEmail(c.getEmail());
                    contact.setPhone(c.getPhone());
                    //contact.setCompany(SecurityContextUtils.loggedUser().getCompanies().get(0));

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
