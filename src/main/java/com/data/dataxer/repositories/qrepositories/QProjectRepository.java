package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QProjectRepository {
    Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long appProfileId);

    Project getById(Long id, Long appProfileId);

    List<Project> search(Long appProfileId, String queryString);

    List<Project> allHasCost(Long appProfileId);

    List<Project> allHasInvoice(Long appProfileId);

    List<Project> allHasPriceOffer(Long appProfileId);

    List<Project> allHasUserTime(String uid, Long appProfileId);

    List<Project> getAllByIds(List<Long> ids, Long appProfileId);

    List<Project> allHasPriceOfferCostInvoice(Long defaultProfileId);
}
