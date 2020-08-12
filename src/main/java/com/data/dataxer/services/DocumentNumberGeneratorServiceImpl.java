package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.DocumentNumberGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class DocumentNumberGeneratorServiceImpl implements DocumentNumberGeneratorService{
    @Override
    public void store(DocumentNumberGenerator documentNumberGenerator) {

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
