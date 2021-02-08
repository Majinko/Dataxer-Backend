package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.Periods;
import com.data.dataxer.repositories.DocumentNumberGeneratorRepository;
import com.data.dataxer.repositories.qrepositories.QDocumentNumberGeneratorRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.DefaultInvoiceNumberGenerator;
import com.data.dataxer.utils.FormatValidator;
import com.data.dataxer.utils.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class DocumentNumberGeneratorServiceImpl implements DocumentNumberGeneratorService {

    private final DocumentNumberGeneratorRepository documentNumberGeneratorRepository;
    private final QDocumentNumberGeneratorRepository qDocumentNumberGeneratorRepository;

    public DocumentNumberGeneratorServiceImpl(DocumentNumberGeneratorRepository documentNumberGeneratorRepository, QDocumentNumberGeneratorRepository qDocumentNumberGeneratorRepository) {
        this.documentNumberGeneratorRepository = documentNumberGeneratorRepository;
        this.qDocumentNumberGeneratorRepository = qDocumentNumberGeneratorRepository;
    }

    @Override
    public void store(DocumentNumberGenerator documentNumberGenerator) {
        FormatValidator.validateFormat(documentNumberGenerator.getFormat(), documentNumberGenerator.getPeriod());
        if (documentNumberGenerator.getIsDefault()) {
            this.handleIfDefaultAlreadyExist(documentNumberGenerator, false);
        }

        documentNumberGenerator.setLastNumber("0");
        this.documentNumberGeneratorRepository.save(documentNumberGenerator);
    }

    @Override
    public DocumentNumberGenerator update(DocumentNumberGenerator documentNumberGenerator) {
        FormatValidator.validateFormat(documentNumberGenerator.getFormat(), documentNumberGenerator.getPeriod());

        if (documentNumberGenerator.getIsDefault()) {
            this.handleIfDefaultAlreadyExist(documentNumberGenerator, true);
        }
        Optional<DocumentNumberGenerator> oldDocumentNumberGenerator = this.qDocumentNumberGeneratorRepository.getById(documentNumberGenerator.getId(), SecurityUtils.companyIds());

        oldDocumentNumberGenerator.ifPresent(numberGenerator -> documentNumberGenerator.setLastNumber(numberGenerator.getLastNumber()));

        return this.documentNumberGeneratorRepository.save(documentNumberGenerator);
    }

    @Override
    public Page<DocumentNumberGenerator> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qDocumentNumberGeneratorRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.companyIds());
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
    public String generateNextNumberByDocumentType(DocumentType documentType, boolean storeGenerated) {
        DocumentNumberGenerator documentNumberGenerator = this.qDocumentNumberGeneratorRepository.getDefaultByDocumentType(documentType, SecurityUtils.companyIds());

        if (documentNumberGenerator == null) {
            documentNumberGenerator = this.documentNumberGeneratorRepository.save(this.returnDefault(documentType));
        }

        return this.generateNextDocumentNumber(documentNumberGenerator, storeGenerated);
    }

    @Override
    public String generateNextNumberByDocumentTypeFromString(String type) {
        return this.generateNextNumberByDocumentType(DocumentType.valueOf(type), true);
    }

    private DocumentNumberGenerator returnDefault(DocumentType type) {
        return new DocumentNumberGenerator("Default number generator", "YYYYNNNNNN", type, Periods.YEAR, true, "000000");
    }

    @Override
    public String generateNextNumberByDocumentId(Long id, boolean storeGenerated) {
        DocumentNumberGenerator documentNumberGenerator = this.qDocumentNumberGeneratorRepository
                .getById(id, SecurityUtils.companyIds())
                .orElseThrow(() -> new RuntimeException("Generator not found"));
        return this.generateNextDocumentNumber(documentNumberGenerator, storeGenerated);
    }

    @Override
    public String getNextNumber(DocumentNumberGenerator documentNumberGenerator) {
        return this.generateNextDocumentNumber(documentNumberGenerator, false);
    }

    @Override
    public void resetGenerationByType(DocumentType documentType) {
        DocumentNumberGenerator documentNumberGenerator = this.qDocumentNumberGeneratorRepository.getDefaultByDocumentType(documentType, SecurityUtils.companyIds());
        if (documentNumberGenerator != null) {
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

        if (storeGenerated) {
            documentNumberGenerator.setLastNumber(nextNumber);
            generatorChanged = true;
        }
        if (nextNumber.length() > StringUtils.countCharacters(documentNumberGenerator.getFormat(), 'N')) {
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
        int countOfYear = StringUtils.countCharacters(generatedNumber, 'Y');

        if (countOfYear <= 0) {
            return generatedNumber;
        }

        String yearForReplace = String.valueOf(currentDate.getYear());
        if (countOfYear == 2) {
            yearForReplace = yearForReplace.substring(2);
        }
        String forReplace = StringUtils.generateString("Y", countOfYear);
        return generatedNumber.replace(forReplace, yearForReplace);
    }

    private String replaceMonth(String generatedNumber, LocalDate currentDate) {
        int countOfMonth = StringUtils.countCharacters(generatedNumber, 'M');

        if (countOfMonth <= 0) {
            return generatedNumber;
        }

        String monthForReplace = String.valueOf(currentDate.getMonthValue());
        while (monthForReplace.length() < countOfMonth) {
            monthForReplace = "0" + monthForReplace;
        }
        String forReplace = StringUtils.generateString("M", countOfMonth);

        return generatedNumber.replace(forReplace, monthForReplace);
    }

    private String getNextNumber(String generatedNumber, String lastNumber) {
        int countOfNumber = StringUtils.countCharacters(generatedNumber, 'N');

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
        int countOfNumber = StringUtils.countCharacters(generatedNumber, 'N');

        if (countOfNumber == 0 || newNumber.isEmpty()) {
            return generatedNumber;
        }

        String forReplace = StringUtils.generateString("N", countOfNumber);

        return generatedNumber.replace(forReplace, newNumber);
    }

    private String replaceQuarter(String generatedNumber, LocalDate currentDate) {
        int countOfQuarter = StringUtils.countCharacters(generatedNumber, 'Q');

        if (countOfQuarter == 0) {
            return generatedNumber;
        }

        int month = currentDate.getMonthValue();
        String quarterForReplace = "";

        if (month < 4) {
            quarterForReplace = "1";
        }
        if (month >= 4 && month < 7) {
            quarterForReplace = "2";
        }
        if (month >= 7 && month < 10) {
            quarterForReplace = "3";
        }
        if (month >= 10) {
            quarterForReplace = "4";
        }

        while (quarterForReplace.length() < countOfQuarter) {
            quarterForReplace = "0" + quarterForReplace;
        }
        String forReplace = StringUtils.generateString("Q", countOfQuarter);

        return generatedNumber.replace(forReplace, quarterForReplace);
    }

    private String replaceHalfYear(String generatedNumber, LocalDate currentDate) {
        int countOfHalfYear = StringUtils.countCharacters(generatedNumber, 'H');

        if (countOfHalfYear <= 0) {
            return generatedNumber;
        }

        StringBuilder halfYearForReplace = new StringBuilder("2");
        if (currentDate.getMonthValue() < 7) {
            halfYearForReplace = new StringBuilder("1");
        }
        while (halfYearForReplace.length() < countOfHalfYear) {
            halfYearForReplace.insert(0, "0");
        }
        String forReplace = StringUtils.generateString("H", countOfHalfYear);

        return generatedNumber.replace(forReplace, halfYearForReplace.toString());
    }

    private String replaceDay(String generatedNumber, LocalDate currentDate) {
        int countOfDay = StringUtils.countCharacters(generatedNumber, 'D');

        if (countOfDay <= 0) {
            return generatedNumber;
        }

        StringBuilder dayForReplace = new StringBuilder(String.valueOf(currentDate.getDayOfMonth()));
        while (dayForReplace.length() < countOfDay) {
            dayForReplace.insert(0, "0");
        }
        String forReplace = StringUtils.generateString("D", countOfDay);

        return generatedNumber.replace(forReplace, dayForReplace.toString());
    }

    private void handleIfDefaultAlreadyExist(DocumentNumberGenerator documentNumberGenerator, boolean isUpdated) {
        DocumentNumberGenerator defaultNumberGenerator = this.qDocumentNumberGeneratorRepository.getDefaultByDocumentType(documentNumberGenerator.getType(), SecurityUtils.companyIds());
        if (defaultNumberGenerator != null) {
            if (isUpdated && documentNumberGenerator.getId().equals(defaultNumberGenerator.getId())) {
                return;
            }
            defaultNumberGenerator.setIsDefault(Boolean.FALSE);
            update(defaultNumberGenerator);
        }
    }

}
