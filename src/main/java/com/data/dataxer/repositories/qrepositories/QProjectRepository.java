package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QProjectRepository {

    Page<Project> paginate(Pageable pageable, String rqlFilter, String sortExpression, List<Long> companyIds);

    Project getById(Long id, List<Long> companyIds);

    List<Project> search(List<Long> companyIds, String queryString);

    List<Project> allHasCost(List<Long> companyIds);

    List<Project> allHasInvoice(List<Long> companyIds);

    List<Project> allHasPriceOffer(List<Long> companyIds);

    List<Project> allHasUserTime(String uid, List<Long> companyIds);

    List<Project> getAllByIds(List<Long> ids, List<Long> companyIds);
}
