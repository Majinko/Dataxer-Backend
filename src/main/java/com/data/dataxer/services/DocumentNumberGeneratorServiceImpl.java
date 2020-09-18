package com.data.dataxer.services;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.repositories.DocumentNumberGeneratorRepository;
import com.data.dataxer.repositories.qrepositories.QDocumentNumberGeneratorRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.DefaultInvoiceNumberGenerator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DocumentNumberGeneratorServiceImpl implements DocumentNumberGeneratorService{

    private final DocumentNumberGeneratorRepository documentNumberGeneratorRepository;
    private final QDocumentNumberGeneratorRepository qDocumentNumberGeneratorRepository;

    public DocumentNumberGeneratorServiceImpl(DocumentNumberGeneratorRepository documentNumberGeneratorRepository, QDocumentNumberGeneratorRepository qDocumentNumberGeneratorRepository) {
        this.documentNumberGeneratorRepository = documentNumberGeneratorRepository;
        this.qDocumentNumberGeneratorRepository = qDocumentNumberGeneratorRepository;
    }

    @Override
    public void storeOrUpdate(DocumentNumberGenerator documentNumberGenerator) {
        Optional<DocumentNumberGenerator> documentNumberGeneratorOptional = this.qDocumentNumberGeneratorRepository
                .getByDocumentType(documentNumberGenerator.getType(), SecurityUtils.companyIds());
        if (documentNumberGeneratorOptional.isPresent()) {
            documentNumberGenerator.setId(documentNumberGeneratorOptional.get().getId());
            documentNumberGenerator.setLastNumber(documentNumberGeneratorOptional.get().getLastNumber());
        } else {
            documentNumberGenerator.setLastNumber("0");
        }
        this.documentNumberGeneratorRepository.save(documentNumberGenerator);
    }

    @Override
    public DocumentNumberGenerator update(DocumentNumberGenerator documentNumberGenerator) {
        return this.documentNumberGeneratorRepository.save(documentNumberGenerator);
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
    public String generateNextNumberByDocumentType(DocumentType documentType) {
        Optional<DocumentNumberGenerator> documentNumberGeneratorOptional = this.qDocumentNumberGeneratorRepository
                .getByDocumentType(documentType, SecurityUtils.companyIds());

        DocumentNumberGenerator documentNumberGenerator;
        if (!documentNumberGeneratorOptional.isPresent()) {
            documentNumberGenerator = new DocumentNumberGenerator(
                    DefaultInvoiceNumberGenerator.title,
                    DefaultInvoiceNumberGenerator.format,
                    DefaultInvoiceNumberGenerator.type,
                    DefaultInvoiceNumberGenerator.period,
                    DefaultInvoiceNumberGenerator.isDefault,
                    DefaultInvoiceNumberGenerator.lastNumber
            );
        } else {
            documentNumberGenerator = documentNumberGeneratorOptional.get();
        }

        return this.generateNextDocumentNumber(documentNumberGenerator, true);
    }

    @Override
    public String getNextNumber(DocumentNumberGenerator documentNumberGenerator) {
        return this.generateNextDocumentNumber(documentNumberGenerator, false);
    }

    @Override
    public void resetGenerationByType(DocumentType documentType) {
        Optional<DocumentNumberGenerator> documentNumberGeneratorOptional = this.qDocumentNumberGeneratorRepository
                .getByDocumentType(documentType, SecurityUtils.companyIds());
        if (documentNumberGeneratorOptional.isPresent()) {
            DocumentNumberGenerator documentNumberGenerator = documentNumberGeneratorOptional.get();
            documentNumberGenerator.setLastNumber("0");
            this.documentNumberGeneratorRepository.save(documentNumberGenerator);
        }
    }

    @Override
    public void resetGenerationById(Long id) {
        Optional<DocumentNumberGenerator> documentNumberGeneratorOptional = this.qDocumentNumberGeneratorRepository
                .getByIdSimple(id, SecurityUtils.companyIds());
        if (documentNumberGeneratorOptional.isPresent()) {
            DocumentNumberGenerator documentNumberGenerator = documentNumberGeneratorOptional.get();
            documentNumberGenerator.setLastNumber("0");
            this.documentNumberGeneratorRepository.save(documentNumberGenerator);
        } else {
            throw new RuntimeException("Number generator with id {" + id + "} not found");
        }
    }

    private String generateNextDocumentNumber(DocumentNumberGenerator documentNumberGenerator, boolean storeGenerated) {
        LocalDate currentDate = LocalDate.now();
        String generatedNumber = this.replaceYear(documentNumberGenerator.getFormat(), currentDate);
        generatedNumber = this.replaceMonth(generatedNumber, currentDate);
        generatedNumber = this.replaceQuarter(generatedNumber, currentDate);
        generatedNumber = this.replaceHalfYear(generatedNumber, currentDate);
        generatedNumber = this.replaceDay(generatedNumber, currentDate);

        return this.replaceNumber(generatedNumber, getAndStoreNextNumber(documentNumberGenerator, storeGenerated));
    }

    private String getAndStoreNextNumber(DocumentNumberGenerator documentNumberGenerator, boolean storeGenerated) {
        boolean generatorChanged = false;
        String nextNumber = this.getNextNumber(documentNumberGenerator.getFormat(), documentNumberGenerator.getLastNumber());

        if(storeGenerated) {
            documentNumberGenerator.setLastNumber(nextNumber);
            generatorChanged = true;
        }
        if (nextNumber.length() > documentNumberGenerator.getFormat().chars().filter(ch -> ch == 'N').count()) {
            documentNumberGenerator.setFormat(this.extendFormat(documentNumberGenerator.getFormat()));
            generatorChanged = true;
        }
        if (generatorChanged) {
            this.documentNumberGeneratorRepository.save(documentNumberGenerator);
        }

        return nextNumber;
    }

    private String extendFormat(String shorterFormat) {
        int countOfNumber = (int) shorterFormat.chars().filter(ch -> ch == 'N').count();
        int indexOfNumber = shorterFormat.indexOf("N");

        String newFormat = shorterFormat.substring(0, indexOfNumber)
                + shorterFormat.substring(indexOfNumber, (indexOfNumber + countOfNumber))
                + "N"
                + shorterFormat.substring((indexOfNumber + countOfNumber), shorterFormat.length());
        return newFormat;
    }

    private String replaceYear(String generatedNumber, LocalDate currentDate) {
        int countOfYear = (int) generatedNumber.chars().filter(ch -> ch == 'Y').count();

        if (countOfYear == 0) {
            return generatedNumber;
        }

        int indexOfYear = generatedNumber.indexOf('Y');

        String year = String.valueOf(currentDate.getYear());
        String yearFormat = generatedNumber.substring(indexOfYear, (indexOfYear + countOfYear));
        if (countOfYear < 4) {
            return generatedNumber.replace(yearFormat, year.substring(2));
        } else {
            return generatedNumber.replace(yearFormat, year);
        }
    }

    private String replaceMonth(String generatedNumber, LocalDate currentDate) {
        int countOfMonth = (int) generatedNumber.chars().filter(ch -> ch == 'M').count();

        if (countOfMonth == 0) {
            return generatedNumber;
        }

        int indexOfMonth = generatedNumber.indexOf('M');
        int month = currentDate.getMonthValue();

        String monthPart = generatedNumber.substring(indexOfMonth, (indexOfMonth + countOfMonth));
        if (month < 10) {
            return generatedNumber.replace(monthPart, "0" + month);
        } else {
            return generatedNumber.replace(monthPart, String.valueOf(month));
        }
    }

    private String getNextNumber(String generatedNumber, String lastNumber) {
        int countOfNumber = (int) generatedNumber.chars().filter(ch -> ch == 'N').count();

        if (countOfNumber == 0) {
            return "";
        }

        String nextNumber = String.valueOf((Integer.valueOf(lastNumber).intValue() + 1));
        for (int i = nextNumber.length(); i < countOfNumber; i++) {
            nextNumber = "0" + nextNumber;
        }
        return nextNumber;
    }

    private String replaceNumber(String generatedNumber, String newNumber) {
        int countOfNumber = (int) generatedNumber.chars().filter(ch -> ch == 'N').count();

        if (countOfNumber == 0 || newNumber.isEmpty()) {
            return generatedNumber;
        }

        int indexOfNumber = generatedNumber.indexOf('N');

        return generatedNumber.replace(generatedNumber.substring(indexOfNumber, (indexOfNumber + countOfNumber)), newNumber);
    }

    private String replaceQuarter(String generatedNumber, LocalDate currentDate) {
        int countOfQuarter = (int) generatedNumber.chars().filter(ch -> ch == 'Q').count();

        if (countOfQuarter == 0) {
            return generatedNumber;
        }

        int indexOfQuarter = generatedNumber.indexOf('Q');
        int month = currentDate.getMonthValue();

        String quarter = "";
        if (month < 4) {
            quarter = "1";
        }
        if (month >= 4 && month < 7) {
            quarter = "2";
        }
        if (month >= 7 && month < 10) {
            quarter = "3";
        }
        if (month >= 10) {
            quarter = "4";
        }

        if (countOfQuarter > 1) {
            quarter = "0" + quarter;
        }

        return generatedNumber.replace(generatedNumber.substring(indexOfQuarter, (indexOfQuarter + countOfQuarter)), quarter);
    }

    private String replaceHalfYear(String generatedNumber, LocalDate currentDate) {
        int countOfHalfYear = (int) generatedNumber.chars().filter(ch -> ch == 'H').count();

        if (countOfHalfYear == 0) {
            return generatedNumber;
        }

        int indexOfHalfYear = generatedNumber.indexOf('H');
        int month = currentDate.getMonthValue();

        String halfYear = "2";
        if (month < 7) {
            halfYear = "1";
        }

        if (countOfHalfYear > 1) {
            halfYear = "0" + halfYear;
        }

        return generatedNumber.replace(generatedNumber.substring(indexOfHalfYear, (indexOfHalfYear + countOfHalfYear)), halfYear);
    }

    private String replaceDay(String generatedNumber, LocalDate currentDate) {
        int countOfDay = (int) generatedNumber.chars().filter(ch -> ch == 'D').count();

        if (countOfDay == 0) {
            return generatedNumber;
        }

        int indexOfDay = generatedNumber.indexOf('H');
        String day = String.valueOf(currentDate.getDayOfMonth());

        if (day.length() < 2) {
            day = "0" + day;
        }

        return generatedNumber.replace(generatedNumber.substring(indexOfDay, (indexOfDay + countOfDay)), day);
    }
}
