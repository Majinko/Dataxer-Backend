package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.DocumentRelationsRepository;
import com.data.dataxer.repositories.qrepositories.QDocumentBaseRepository;
import com.data.dataxer.repositories.qrepositories.QDocumentRelationsRepository;
import com.data.dataxer.repositories.qrepositories.QInvoiceRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DocumentRelationServiceImpl implements DocumentRelationService {
    private final DocumentRelationsRepository documentRelationsRepository;
    private final QDocumentRelationsRepository qDocumentRelationsRepository;
    private final QInvoiceRepository qInvoiceRepository;
    private final QDocumentBaseRepository qDocumentBaseRepository;

    public DocumentRelationServiceImpl(DocumentRelationsRepository documentRelationsRepository, QDocumentRelationsRepository qDocumentRelationsRepository,
                                       QInvoiceRepository qInvoiceRepository, QDocumentBaseRepository qDocumentBaseRepository) {
        this.documentRelationsRepository = documentRelationsRepository;
        this.qDocumentRelationsRepository = qDocumentRelationsRepository;
        this.qInvoiceRepository = qInvoiceRepository;
        this.qDocumentBaseRepository = qDocumentBaseRepository;
    }

    @Override
    public void store(Long originalDocumentId, Long relatedDocumentId) {
        DocumentRelations documentRelation = new DocumentRelations();
        documentRelation.setDocumentId(originalDocumentId);
        documentRelation.setRelationDocumentId(relatedDocumentId);
        this.storeDocumentRelation(documentRelation);
    }

    @Override
    @Transactional
    public void storeDocumentRelation(DocumentRelations documentRelations) {
        this.documentRelationsRepository.save(documentRelations);
    }

    @Override
    public DocumentRelations getById(Long id) {
        return this.qDocumentRelationsRepository.getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Document relation not found"));
    }

    @Override
    public Long getOriginalDocumentId(Long relativeDocumentId) {
        List<DocumentRelations> documentRelations = this.documentRelationsRepository.findOriginalDocumentIdByRelative(relativeDocumentId, SecurityUtils.companyIds());
        if (documentRelations == null) {
            throw new RuntimeException("Not found original document to relation document");
        }
        if (documentRelations.size() > 1) {
            throw new RuntimeException("Document with id " + relativeDocumentId +
                    " is related to too many documents [" + documentRelations.size() + "]");
        }
        if (documentRelations.isEmpty()) {
            throw new RuntimeException("Too many original documents to relation document");
        }
        return documentRelations.get(0).getDocumentId();
    }

    @Override
    public List<Long> getAllRelationDocumentIds(Long originalDocumentId) {
        List<Long> result = new ArrayList<>();
        List<DocumentRelations> documentRelations = this.documentRelationsRepository.findAllRelationDocuments(originalDocumentId);
        for (DocumentRelations documentRelation : documentRelations) {
            result.add(documentRelation.getRelationDocumentId());
        }
        return result;
    }

    @Override
    public List<Invoice> getAllRelatedDocuments(Long originalDocumentId) {
        List<Invoice> result = new ArrayList<>();
        List<DocumentRelations> documentRelations = this.documentRelationsRepository.findAllRelationDocuments(originalDocumentId);
        for (DocumentRelations documentRelation : documentRelations) {
            Optional<Invoice> optionalInvoice = this.qInvoiceRepository.getById(documentRelation.getRelationDocumentId(), SecurityUtils.companyIds());
            optionalInvoice.ifPresent(result::add);
        }
        return result;
    }

    @Override
    public List<Invoice> getAllRelationDocumentsByDocumentType(Long originalDocumentId, DocumentType documentType) {
        List<Invoice> result = new ArrayList<>();
        List<DocumentRelations> documentRelations = this.documentRelationsRepository.findAllRelationDocuments(originalDocumentId);
        for (DocumentRelations documentRelation : documentRelations) {
            Invoice optionalRelatedInvoice = this.qInvoiceRepository.getById(documentRelation.getRelationDocumentId(), SecurityUtils.companyIds()).orElse(null);
            if (optionalRelatedInvoice != null && optionalRelatedInvoice.getDocumentType().equals(documentType)) {
                result.add(optionalRelatedInvoice);
            }
        }
        return result;
    }

    @Override
    public List<DocumentRelation> getRelatedDocuments(Long id) {
        List<DocumentBase> documents = this.qDocumentBaseRepository.getAllDocumentByIds(this.documentRelationsRepository.findAllRelationDocuments(id).stream().map(DocumentRelations::getRelationDocumentId).collect(Collectors.toList()), SecurityUtils.companyId());

        return documents.stream().map(documentsBase -> {

            DocumentRelation documentRelation = new DocumentRelation();
            documentRelation.setRelatedDocumentId(documentsBase.getId());
            documentRelation.setDocumentTitle(documentsBase.getTitle());
            return documentRelation;

        }).collect(Collectors.toList());
    }
}
