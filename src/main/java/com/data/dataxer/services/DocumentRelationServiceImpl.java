package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.dto.DocumentRelationDTO;
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
    public void store(Long originalDocumentId, Long relatedDocumentId) {
        DocumentRelations documentRelation = new DocumentRelations();
        documentRelation.setDocumentId(originalDocumentId);
        documentRelation.setRelationDocumentId(relatedDocumentId);

        this.documentRelationsRepository.save(documentRelation);
    }

    @Override
    public List<DocumentRelationDTO> getRelatedDocuments(Long id) {
        List<DocumentBase> documents = this.qDocumentBaseRepository.getAllDocumentByIds(this.documentRelationsRepository.findAllRelationDocuments(id, SecurityUtils.companyId()).stream().map(DocumentRelations::getRelationDocumentId).collect(Collectors.toList()), SecurityUtils.companyId());

        return documents.stream().map(documentsBase -> {

            DocumentRelationDTO documentRelation = new DocumentRelationDTO();
            documentRelation.setRelatedDocumentId(documentsBase.getId());
            documentRelation.setDocumentTitle(documentsBase.getTitle());
            documentRelation.setDocumentType(documentsBase.getDocumentType());

            return documentRelation;

        }).collect(Collectors.toList());
    }

    @Override
    public void destroy(Long documentId, Long relationDocumentId) {
        DocumentRelations documentRelations = this.documentRelationsRepository.findAllByDocumentIdAndAndRelationDocumentIdAAndCompanyId(
                documentId, relationDocumentId, SecurityUtils.companyId()
        );

        documentRelationsRepository.delete(documentRelations);
    }
}
