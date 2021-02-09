package com.data.dataxer.repositories.qrepositories;

import com.data.dataxer.models.domain.DocumentBase;

import java.util.List;

public interface QDocumentBaseRepository {

    List<DocumentBase> getAllDocumentByIds(List<Long> documentIds, Long companyId);

    List<DocumentBase> getAllByQueryString(String search, Long companyId);

}
