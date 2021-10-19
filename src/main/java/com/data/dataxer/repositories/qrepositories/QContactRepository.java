package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QContactRepository {
    List<Contact> allWithProjects(List<Long> companyIds);

    Page<Contact> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds);

    Optional<Contact> getById(Long id, List<Long> companyIds);

    List<Contact> getAllByIds(List<Long> contactIds, List<Long> companyIds);

    List<Contact> allHasCost(List<Long> companyIds);

    List<Contact> allHasInvoice(List<Long> companyIds);

    List<Contact> allHasPriceOffer(List<Long> companyIds);

    List<Contact> allHasProject(List<Long> companyIds);
}
