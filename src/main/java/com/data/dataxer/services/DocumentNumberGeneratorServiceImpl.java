package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.Periods;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.DocumentNumberGeneratorRepository;
import com.data.dataxer.repositories.qrepositories.QCostRepository;
import com.data.dataxer.repositories.qrepositories.QDocumentNumberGeneratorRepository;
import com.data.dataxer.repositories.qrepositories.QInvoiceRepository;
import com.data.dataxer.repositories.qrepositories.QPriceOfferRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import com.data.dataxer.utils.FormatValidator;
import com.data.dataxer.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentNumberGeneratorServiceImpl implements DocumentNumberGeneratorService {
    @Autowired
    private DocumentNumberGeneratorRepository documentNumberGeneratorRepository;
    @Autowired
    private QDocumentNumberGeneratorRepository qDocumentNumberGeneratorRepository;
    @Autowired
    private QInvoiceRepository qInvoiceRepository;
    @Autowired
    private QCostRepository qCostRepository;
    @Autowired
    private QPriceOfferRepository qPriceOfferRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Override
    public void store(DocumentNumberGenerator documentNumberGenerator) {
        FormatValidator.validateFormat(documentNumberGenerator.getFormat(), documentNumberGenerator.getPeriod());
        if (documentNumberGenerator.getIsDefault()) {
            this.handleIfDefaultAlreadyExist(documentNumberGenerator, documentNumberGenerator.getCompany().getId(), false);
        }

        this.documentNumberGeneratorRepository.save(documentNumberGenerator);
    }

    @Override
    public DocumentNumberGenerator update(DocumentNumberGenerator documentNumberGenerator) {
        FormatValidator.validateFormat(documentNumberGenerator.getFormat(), documentNumberGenerator.getPeriod());

        if (documentNumberGenerator.getIsDefault()) {
            this.handleIfDefaultAlreadyExist(documentNumberGenerator, documentNumberGenerator.getCompany().getId(), true);
        }

        return this.documentNumberGeneratorRepository.save(documentNumberGenerator);
    }

    @Override
    public Page<DocumentNumberGenerator> paginate(Pageable pageable, String rqlFilter, String sortExpression) {
        return this.qDocumentNumberGeneratorRepository.paginate(pageable, rqlFilter, sortExpression, SecurityUtils.defaultProfileId());
    }

    @Override
    public DocumentNumberGenerator getById(Long id) {
        return this.qDocumentNumberGeneratorRepository
                .getById(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Document number generator not found"));
    }

    @Override
    public DocumentNumberGenerator getByIdSimple(Long id) {
        return this.qDocumentNumberGeneratorRepository
                .getByIdSimple(id, SecurityUtils.defaultProfileId())
                .orElseThrow(() -> new RuntimeException("Document number generator not found"));
    }

    @Override
    public void destroy(Long id) {
        this.documentNumberGeneratorRepository.delete(this.getByIdSimple(id));
    }

    @Override
    public String generateNextNumberByDocumentType(DocumentType documentType, Long companyId) {
        DocumentNumberGenerator documentNumberGenerator = this.qDocumentNumberGeneratorRepository.getDefaultByDocumentType(documentType, companyId, SecurityUtils.defaultProfileId());

        if (documentNumberGenerator == null) {
            documentNumberGenerator = this.documentNumberGeneratorRepository.save(this.returnDefault(documentType, companyId));
        }

        return this.generateNextDocumentNumber(documentNumberGenerator, documentType);
    }


    private DocumentNumberGenerator returnDefault(DocumentType type, Long companyId) {
        Optional<Company> companyOptional = companyRepository.findByAppProfileIdAndId(companyId, SecurityUtils.defaultProfileId());

        if (companyOptional.isPresent()) {
            return new DocumentNumberGenerator("Default number generator", "YYYYNNNNNN", type, Periods.YEAR, true, companyOptional.get());
        }

        throw new RuntimeException("Unauthorized action");
    }

    @Override
    public String getNextNumber(DocumentNumberGenerator documentNumberGenerator) {
        return this.generateNextDocumentNumber(documentNumberGenerator, documentNumberGenerator.getType());
    }

    private String generateNextDocumentNumber(DocumentNumberGenerator documentNumberGenerator, DocumentType type) {
        LocalDate currentDate = LocalDate.now();
        String generatedNumber = this.replaceYear(documentNumberGenerator.getFormat(), currentDate);
        generatedNumber = this.replaceMonth(generatedNumber, currentDate);
        generatedNumber = this.replaceQuarter(generatedNumber, currentDate);
        generatedNumber = this.replaceHalfYear(generatedNumber, currentDate);
        generatedNumber = this.replaceDay(generatedNumber, currentDate);

        return this.replaceNumber(generatedNumber, getNextNumber(documentNumberGenerator, type, currentDate));
    }

    private String getNextNumber(DocumentNumberGenerator documentNumberGenerator, DocumentType type, LocalDate currentDate) {
        String lastNumber = "0";
        switch (type) {
            case INVOICE:
            case PROFORMA:
            case SUMMARY_INVOICE:
            case TAX_DOCUMENT:
                Invoice invoice = this.qInvoiceRepository.getLastInvoice(type, documentNumberGenerator.getCompany().getId(), SecurityUtils.defaultProfileId());
                if (invoice != null) {
                    lastNumber = invoice.getNumber();
                }
                break;
            case COST:
                Cost cost = this.qCostRepository.getLastCost(documentNumberGenerator.getCompany().getId(), SecurityUtils.defaultProfileId());
                if (cost != null) {
                    lastNumber = cost.getNumber();
                }
                break;
            case PRICE_OFFER:
            default:
                PriceOffer priceOffer = this.qPriceOfferRepository.getLastPriceOffer(documentNumberGenerator.getCompany().getId(), SecurityUtils.defaultProfileId());
                if (priceOffer != null) {
                    lastNumber = priceOffer.getNumber();
                }
                break;
        }
        String nextNumber = this.getNextNumber(documentNumberGenerator.getFormat(), lastNumber, currentDate);

        if (nextNumber.length() > StringUtils.countCharacters(documentNumberGenerator.getFormat(), 'N')) {
            documentNumberGenerator.setFormat(this.extendFormat(documentNumberGenerator.getFormat()));
            this.documentNumberGeneratorRepository.save(documentNumberGenerator);
        }

        return nextNumber;
    }

    private String parseNumber(String format, String lastNumber) {
        int countOfNumber = StringUtils.countCharacters(format, 'N');
        int firstPosition = format.indexOf('N');

        return lastNumber.substring(firstPosition, firstPosition + countOfNumber);
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

    private String getNextNumber(String format, String lastNumber, LocalDate currentDate) {
        int countOfNumber = StringUtils.countCharacters(format, 'N');

        if (countOfNumber == 0) {
            return "";
        }

        if (hasResetNumber(format, lastNumber, currentDate)) {
            lastNumber = "0";
        } else {
            lastNumber = parseNumber(format, lastNumber);
        }

        String nextNumber = String.valueOf((Integer.parseInt(lastNumber) + 1));
        for (int i = nextNumber.length(); i < countOfNumber; i++) {
            nextNumber = "0" + nextNumber;
        }
        return nextNumber;
    }

    private boolean hasResetNumber(String format, String lastNumber, LocalDate currentDate) {
        int positionOfYear = format.indexOf('Y');
        int countOfYear = StringUtils.countCharacters(format, 'Y');

        //format neobsahuje rok alebo este nie je ziadne cislo a teda je uz resetovane
        if (positionOfYear == -1 || lastNumber.equals("0")) {
            return true;
        }

        String lastNumberYear = lastNumber.substring(positionOfYear, positionOfYear + countOfYear);

        if (countOfYear == 2) {
            return !lastNumberYear.equals(String.valueOf(currentDate.getYear()).substring(2));
        } else {
            return !lastNumberYear.equals(String.valueOf(currentDate.getYear()));
        }
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

    private void handleIfDefaultAlreadyExist(DocumentNumberGenerator documentNumberGenerator, Long companyId, boolean isUpdated) {
        DocumentNumberGenerator defaultNumberGenerator = this.qDocumentNumberGeneratorRepository.getDefaultByDocumentType(documentNumberGenerator.getType(), companyId, SecurityUtils.defaultProfileId());
        if (defaultNumberGenerator != null) {
            if (isUpdated && documentNumberGenerator.getId().equals(defaultNumberGenerator.getId())) {
                return;
            }
            defaultNumberGenerator.setIsDefault(Boolean.FALSE);
            update(defaultNumberGenerator);
        }
    }
}
