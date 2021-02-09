package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.repositories.DocumentRelationsRepository;
import com.data.dataxer.repositories.qrepositories.QDocumentBaseRepository;;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentRelationServiceImpl implements DocumentRelationService {
    private final DocumentRelationsRepository documentRelationsRepository;
    private final QDocumentBaseRepository qDocumentBaseRepository;

    public DocumentRelationServiceImpl(DocumentRelationsRepository documentRelationsRepository, QDocumentBaseRepository qDocumentBaseRepository) {
        this.documentRelationsRepository = documentRelationsRepository;
        this.qDocumentBaseRepository = qDocumentBaseRepository;
    }

    @Override
    public DocumentRelation store(Long originalDocumentId, Long relatedDocumentId) {
        DocumentRelation documentRelation = new DocumentRelation();
        documentRelation.setDocumentId(originalDocumentId);
        documentRelation.setRelationDocumentId(relatedDocumentId);

        return this.documentRelationsRepository.save(documentRelation);
    }

    @Override
    public List<DocumentBase> getRelatedDocuments(Long id) {
        return this.qDocumentBaseRepository.getAllDocumentByIds(this.documentRelationsRepository.findAllRelationDocuments(id, SecurityUtils.companyId()).stream().map(DocumentRelation::getRelationDocumentId).collect(Collectors.toList()), SecurityUtils.companyId());
    }

    @Override
    public List<DocumentBase> search(String queryString) {
        return this.qDocumentBaseRepository.getAllByQueryString(queryString, SecurityUtils.companyId());
    }

    @Override
    public void destroy(Long documentId, Long relationDocumentId) {
        DocumentRelation documentRelation = this.documentRelationsRepository.findAllByDocumentIdAndRelationDocumentIdAndCompanyId(
                documentId, relationDocumentId, SecurityUtils.companyId()
        );

        documentRelationsRepository.delete(documentRelation);
    }
}
