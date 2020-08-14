package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.Periods;
import com.data.dataxer.repositories.DocumentNumberGeneratorRepository;
import com.data.dataxer.repositories.qrepositories.QDocumentNumberGeneratorRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

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
        this.documentNumberGeneratorRepository.save(documentNumberGenerator);
    }

    @Override
    public Page<DocumentNumberGenerator> paginate(Pageable pageable, Filter filter) {
        return this.qDocumentNumberGeneratorRepository.paginate(pageable, filter, SecurityUtils.companyIds());
    }

    @Override
    public DocumentNumberGenerator getById(Long id) {
        return this.qDocumentNumberGeneratorRepository
                .getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Document number generator not found"));
    }

    @Override
    public DocumentNumberGenerator getByIdSimple(Long id) {
        return this.qDocumentNumberGeneratorRepository
                .getByIdSimple(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Document number generator not found"));
    }

    @Override
    public void destroy(Long id) {
        this.documentNumberGeneratorRepository.delete(this.getByIdSimple(id));
    }

    @Override
    public String generateNextNumber(Long id) {
        DocumentNumberGenerator documentNumberGenerator = this.getByIdSimple(id);

        return this.generateNextDocumentNumber(documentNumberGenerator.getLastNumber(),
                documentNumberGenerator.getFormat(), documentNumberGenerator.getPeriod());
    }

    private String generateNextDocumentNumber(String lastNumber, String format, Periods period) {
        switch(period) {
            case MONTHLY:
                return this.generateNextDocumentNumberMonthly(lastNumber, format);
            case QUARTER:
                return this.generateNextDocumentNumberQuarter(lastNumber, format);
            case HALF_YEAR:
                return this.generateNextDocumentNumberHalfYear(lastNumber, format);
            case YEAR:
            default:
                return this.generateNextDocumentNumberYear(lastNumber, format);
        }
    }

    private String generateNextDocumentNumberMonthly(String lastNumber, String format) {
        String generatedNumber = format;

        long countOfYear = format.chars().filter(ch -> ch == 'Y').count();
        int indexOfMonth = format.indexOf('M');
        int countOfMonth = (int) format.chars().filter(ch -> ch == 'M').count();
        int indexOfNumber = format.indexOf('N');
        int countOfNumber = (int) format.chars().filter(ch -> ch == 'N').count();

        LocalDate currentDate = LocalDate.now();
        String year = String.valueOf(currentDate.getYear());
        int month = currentDate.getMonthValue();

        if (countOfYear < 4) {
            generatedNumber.replace("YY", year.substring(2));
        } else {
            generatedNumber.replace("YYYY", year);
        }

        String replaceableMonth = "";
        if (month < 10) {
            replaceableMonth = "0" + String.valueOf(month);
        } else {
            replaceableMonth = String.valueOf(month);
        }
        generatedNumber.replace(generatedNumber.substring(indexOfMonth, (indexOfMonth + countOfMonth)), replaceableMonth);

        int nextValue = Integer.valueOf(lastNumber).intValue() + 1;

        return generatedNumber;
    }

    private String generateNextDocumentNumberQuarter(String lastNumber, String format) {
        String generatedNumber = "";
        return generatedNumber;
    }

    private String generateNextDocumentNumberHalfYear(String lastNumber, String format) {
        String generatedNumber = "";
        return generatedNumber;
    }

    private String generateNextDocumentNumberYear(String lastNumber, String format) {
        String generatedNumber = "";
        return generatedNumber;
    }
}
