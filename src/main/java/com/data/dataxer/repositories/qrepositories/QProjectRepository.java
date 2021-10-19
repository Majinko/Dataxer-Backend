package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QProjectRepository {

    Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression, Long companyId);

    Project getById(Long id, Long companyId);

    List<Project> search(Long companyId, String queryString);

    List<Project> allHasCost(List<Long> companyIds);

    List<Project> allHasInvoice(List<Long> companyIds);

    List<Project> allHasPriceOffer(List<Long> companyIds);

    List<Project> allHasUserTime(String uid, List<Long> companyIds);
}
