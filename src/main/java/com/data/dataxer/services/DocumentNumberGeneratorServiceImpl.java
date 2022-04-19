package com.data.dataxer.services;

import com.data.dataxer.models.domain.*;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.Periods;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.DocumentNumberGeneratorRepository;
import com.data.dataxer.repositories.qrepositories.*;
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
    private QDemandRepository qDemandRepository;
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
    public String generateNextNumberByDocumentType(DocumentType documentType, LocalDate date, Long companyId) {
        DocumentNumberGenerator documentNumberGenerator = this.qDocumentNumberGeneratorRepository.getDefaultByDocumentType(documentType, companyId, SecurityUtils.defaultProfileId());

        if (documentNumberGenerator == null) {
            documentNumberGenerator = this.documentNumberGeneratorRepository.save(this.returnDefault(documentType, companyId));
        }

        return this.generateNextDocumentNumber(documentNumberGenerator, date, documentType);
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
        return this.generateNextDocumentNumber(documentNumberGenerator, LocalDate.now(), documentNumberGenerator.getType());
    }

    @Override
    public List<DocumentNumberGenerator> getAll() {
        return this.documentNumberGeneratorRepository.findAllByAppProfileId(SecurityUtils.defaultProfileId());
    }

    private String generateNextDocumentNumber(DocumentNumberGenerator documentNumberGenerator, LocalDate date, DocumentType type) {
        String generatedNumber = this.replaceYear(documentNumberGenerator.getFormat(), date);

        generatedNumber = this.replaceMonth(generatedNumber, date);
        generatedNumber = this.replaceQuarter(generatedNumber, date);
        generatedNumber = this.replaceHalfYear(generatedNumber, date);
        generatedNumber = this.replaceDay(generatedNumber, date);

        return this.replaceNumber(generatedNumber, getNextNumber(documentNumberGenerator, type, date));
    }

    private String getNextNumber(DocumentNumberGenerator documentNumberGenerator, DocumentType type, LocalDate dateToGenerate) {
        String lastNumber = "0";
        switch (type) {
            case PROFORMA:
            case INVOICE:
            case SUMMARY_INVOICE:
            case TAX_DOCUMENT:
                Invoice invoice = this.loadLastInvoiceByPeriod(type, dateToGenerate, documentNumberGenerator.getPeriod(), documentNumberGenerator.getCompany().getId());
                if (invoice != null) {
                    lastNumber = invoice.getNumber();
                }
                break;
            case COST:
                Cost cost = this.loadLastCostByPeriod(dateToGenerate, documentNumberGenerator.getPeriod(), documentNumberGenerator.getCompany().getId());
                if (cost != null) {
                    lastNumber = cost.getNumber();
                }
                break;
            case DEMAND:
                Demand demand = this.loadLastDemandByPeriod(dateToGenerate, documentNumberGenerator.getPeriod(), documentNumberGenerator.getCompany().getId());
                if (demand != null) {
                    lastNumber = demand.getNumber();
                }
                break;
            case PRICE_OFFER:
            default:
                PriceOffer priceOffer = this.loadLastPriceOfferByPeriod(dateToGenerate, documentNumberGenerator.getPeriod(), documentNumberGenerator.getCompany().getId());
                if (priceOffer != null) {
                    lastNumber = priceOffer.getNumber();
                }
                break;
        }
        String nextNumber = this.getNextNumber(documentNumberGenerator.getFormat(), lastNumber, dateToGenerate);

        if (nextNumber.length() > StringUtils.countCharacters(documentNumberGenerator.getFormat(), 'N')) {
            documentNumberGenerator.setFormat(this.extendFormat(documentNumberGenerator.getFormat()));
            this.documentNumberGeneratorRepository.save(documentNumberGenerator);
        }

        return nextNumber;
    }

    private Invoice loadLastInvoiceByPeriod(DocumentType type, LocalDate date, Periods period, Long companyId) {
        List<DocumentType> typesToSearch;

        if (type.equals(DocumentType.PROFORMA)) {
            typesToSearch = List.of(type);
        } else {
            typesToSearch = List.of(DocumentType.INVOICE, DocumentType.SUMMARY_INVOICE, DocumentType.TAX_DOCUMENT);
        }
        switch (period) {
            case DAILY: return this.qInvoiceRepository.getLastInvoiceByDayAndMonthAndYear(typesToSearch, date, companyId, SecurityUtils.defaultProfileId());
            case MONTHLY: return this.qInvoiceRepository.getLastInvoiceByMonthAndYear(typesToSearch, date, companyId, SecurityUtils.defaultProfileId());
            case QUARTER: return this.qInvoiceRepository.getLastInvoiceByQuarterAndYear(typesToSearch, date, companyId, SecurityUtils.defaultProfileId());
            case HALF_YEAR:
            case YEAR:
            default: return this.qInvoiceRepository.getLastInvoiceByYear(typesToSearch, date, companyId, SecurityUtils.defaultProfileId());
        }
    }

    private PriceOffer loadLastPriceOfferByPeriod(LocalDate date, Periods period, Long companyId) {
        switch (period) {
            case DAILY: return this.qPriceOfferRepository.getLastPriceOfferByDayAndMonthAndYear(date, companyId, SecurityUtils.defaultProfileId());
            case MONTHLY: return this.qPriceOfferRepository.getLastPriceOfferByMonthAndYear(date, companyId, SecurityUtils.defaultProfileId());
            case QUARTER: return this.qPriceOfferRepository.getLastPriceOfferByQuarterAndYear(date, companyId, SecurityUtils.defaultProfileId());
            case HALF_YEAR:
            case YEAR:
            default: return this.qPriceOfferRepository.getLastPriceOfferByYear(date, companyId, SecurityUtils.defaultProfileId());
        }
    }

    private Demand loadLastDemandByPeriod(LocalDate date, Periods period, Long companyId) {
        switch (period) {
            case DAILY: return this.qDemandRepository.getLastDemandByDayAndMonthAndYear(date, companyId, SecurityUtils.defaultProfileId());
            case MONTHLY: return this.qDemandRepository.getLastDemandByMonthAndYear(date, companyId, SecurityUtils.defaultProfileId());
            case QUARTER: return this.qDemandRepository.getLastDemandByQuarterAndYear(date, companyId, SecurityUtils.defaultProfileId());
            case HALF_YEAR:
            case YEAR:
            default: return this.qDemandRepository.getLastDemandByYear(date, companyId, SecurityUtils.defaultProfileId());
        }
    }

    private Cost loadLastCostByPeriod(LocalDate date, Periods period, Long companyId) {
        switch (period) {
            case DAILY: return this.qCostRepository.getLastCostByDayAndMonthAndYear(date, companyId, SecurityUtils.defaultProfileId());
            case MONTHLY: return this.qCostRepository.getLastCostByMonthAndYear(date, companyId, SecurityUtils.defaultProfileId());
            case QUARTER: return this.qCostRepository.getLastCostByQuarterAndYear(date, companyId, SecurityUtils.defaultProfileId());
            case HALF_YEAR:
            case YEAR:
            default: return this.qCostRepository.getLastCostOfferByYear(date, companyId, SecurityUtils.defaultProfileId());
        }
    }

    private String parseNumber(String format, String lastNumber) {
        //check if number generator was changed
        if (format.length() != lastNumber.length()) {
            return "0";
        }
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

        //ak sme nacitali dokument potrebujeme cislo vyparsovat
        if (!lastNumber.equals("0")) {
            lastNumber = parseNumber(format, lastNumber);
        }

        String nextNumber = String.valueOf((Integer.parseInt(lastNumber) + 1));
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
