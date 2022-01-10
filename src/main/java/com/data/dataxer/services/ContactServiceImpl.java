package com.data.dataxer.services;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.repositories.ContactRepository;
import com.data.dataxer.repositories.qrepositories.QContactRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
    public Contact store(Contact contact) {
        return contactRepository.save(contact);
    }

    @Override
    public Contact update(Contact c) {
        this.contactRepository.save(c);

        return c;
    }

    @Override
    public Page<Contact> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qContactRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.defaultProfileId());
    }

    @Override
    public Contact getById(Long id) {
        return this.qContactRepository.getById(id, SecurityUtils.defaultProfileId()).orElse(null);
    }

    @Override
    public void destroy(Long id) {
        contactRepository.delete(this.getById(id));
    }

    @Override
    public List<Contact> getContactByIds(List<Long> contactIds) {
        return this.qContactRepository.getAllByIds(contactIds, SecurityUtils.defaultProfileId());
    }

    @Override
    public List<Contact> findAll() {
        return contactRepository.findAllByAppProfileId(SecurityUtils.defaultProfileId()).orElse(null);
    }

    @Override
    public List<Contact> findByName(String name) {
        return contactRepository
                .findFirst5ByNameContainingAndAppProfileId(name, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Contact not found"));
    }

    @Override
    public List<Contact> allHasCost() {
        return this.qContactRepository.allHasCost(SecurityUtils.defaultProfileId());
    }

    @Override
    public List<Contact> allHasInvoice() {
        return this.qContactRepository.allHasInvoice(SecurityUtils.defaultProfileId());
    }

    @Override
    public List<Contact> allHasPriceOffer() {
        return this.qContactRepository.allHasPriceOffer(SecurityUtils.defaultProfileId());
    }

    @Override
    public List<Contact> allHasProject() {
        return this.qContactRepository.allHasProject(SecurityUtils.defaultProfileId());
    }
}
