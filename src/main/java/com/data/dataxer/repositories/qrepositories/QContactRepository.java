package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface QContactRepository {
    List<Contact> allWithProjects(Long appProfileId);

    Page<Contact> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);

    Optional<Contact> getById(Long id, Long appProfileId);

    List<Contact> getAllByIds(List<Long> contactIds, Long appProfileId);

    List<Contact> allHasCost(Long appProfileId);

    List<Contact> allHasInvoice(Long appProfileId);

    List<Contact> allHasPriceOffer(Long appProfileId);

    List<Contact> allHasProject(Long appProfileId);
}
