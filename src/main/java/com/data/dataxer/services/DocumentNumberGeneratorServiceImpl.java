package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.repositories.DocumentNumberGeneratorRepository;
import com.data.dataxer.repositories.qrepositories.QDocumentNumberGeneratorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DocumentNumberGeneratorServiceImpl implements DocumentNumberGeneratorService{

    private final DocumentNumberGeneratorRepository documentNumberGeneratorRepository;
    private final QDocumentNumberGeneratorRepository qDocumentNumberGeneratorRepository;

    public DocumentNumberGeneratorServiceImpl(DocumentNumberGeneratorRepository documentNumberGeneratorRepository, QDocumentNumberGeneratorRepository qDocumentNumberGeneratorRepository) {
        this.documentNumberGeneratorRepository = documentNumberGeneratorRepository;
        this.qDocumentNumberGeneratorRepository = qDocumentNumberGeneratorRepository;
    }

    @Override
    public void store(DocumentNumberGenerator documentNumberGenerator) {
        this.documentNumberGeneratorRepository.save(documentNumberGenerator);
    }

    @Override
    public void update(DocumentNumberGenerator documentNumberGenerator) {

    }

    @Override
    public Page<DocumentNumberGenerator> paginate(Pageable pageable, Filter filter) {
        return null;
    }

    @Override
    public DocumentNumberGenerator getById(Long id) {
        return null;
    }

    @Override
    public DocumentNumberGenerator getByIdSimple(Long id) {
        return null;
    }

    @Override
    public void destroy(Long id) {

    }
}
