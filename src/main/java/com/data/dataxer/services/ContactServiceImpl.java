package com.data.dataxer.services;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.repositories.ContactRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    private final ContactRepository contactRepository;

    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
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
    public Page<Contact> paginate(Pageable pageable) {
        return contactRepository.findAllByDeletedAtIsNull(pageable)
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public Page<Contact> paginateFilter(Pageable pageable, String firstName) {
        return contactRepository.findAllByDeletedAtIsNullAndFirstNameContaining(pageable, firstName).orElse(null);
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
        // do mapu mi pride vysledok metody findById cize kontakt
        contactRepository.findById(id)
                .map(contact -> {
                    contact.setFirstName(c.getFirstName());
                    contact.setLastName(c.getLastName());
                    contact.setStreet(c.getStreet());
                    contact.setTown(c.getTown());
                    contact.setCountry(c.getCountry());
                    contact.setPostalCode(c.getPostalCode());
                    contact.setRegNumber(c.getRegNumber());
                    contact.setEmail(c.getEmail());
                    contact.setPhone(c.getPhone());

                    return contactRepository.save(c);
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
