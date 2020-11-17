package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentRelations;
import com.data.dataxer.repositories.DocumentRelationsRepository;
import com.data.dataxer.repositories.qrepositories.QInvoiceRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentRelationServiceImpl implements DocumentRelationService {

    private final DocumentRelationsRepository documentRelationsRepository;
    private final QInvoiceRepository qInvoiceRepository;

    public DocumentRelationServiceImpl(DocumentRelationsRepository documentRelationsRepository, QInvoiceRepository qInvoiceRepository) {
        this.documentRelationsRepository = documentRelationsRepository;
        this.qInvoiceRepository = qInvoiceRepository;
    }

    @Override
    @Transactional
    public void store(DocumentRelations documentRelations) {
        this.documentRelationsRepository.save(documentRelations);
    }

    @Override
    public Long getOriginalDocumentId(Long relativeDocumentId) {
        List<DocumentRelations> documentRelations = this.documentRelationsRepository.findOriginalDocumentIdByRelative(relativeDocumentId, SecurityUtils.companyIds());
        if (documentRelations == null) {
            throw new RuntimeException("Not found original document to relation document!");
        }
        if (documentRelations.size() > 1) {
            throw new RuntimeException("Document with id " + relativeDocumentId +
                    " is related to too many documents [" + documentRelations.size() + "]");
        }
        if (documentRelations.isEmpty()) {
            throw new RuntimeException("Too many original documents to relation document!");
        }
        return documentRelations.get(0).getDocumentId();
    }

    @Override
    public List<Long> getAllRelationDocuments(Long originalDocumentId) {
        List<Long> result = new ArrayList<>();
        List<DocumentRelations> documentRelations = this.documentRelationsRepository.findAllRelationDocuments(originalDocumentId);
        for (DocumentRelations documentRelation : documentRelations) {
            result.add(documentRelation.getRelationDocumentId());
        }
        return result;
    }
}
