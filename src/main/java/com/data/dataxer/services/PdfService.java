package com.data.dataxer.services;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.DocumentBase;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.domain.PriceOffer;
import com.data.dataxer.models.enums.CompanyTaxType;
import com.data.dataxer.models.enums.DocumentType;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service
public class PdfService {
    private static final String PDF_RESOURCES = "/templates/view/invoice/pdf-resources/";

    private final SpringTemplateEngine templateEngine;
    private final InvoiceService invoiceService;
    private final PriceOfferService priceOfferService;
    private final CompanyService companyService;

    public PdfService(SpringTemplateEngine templateEngine, InvoiceService invoiceService,
                      PriceOfferService priceOfferService, CompanyService companyService) {
        this.templateEngine = templateEngine;
        this.invoiceService = invoiceService;
        this.priceOfferService = priceOfferService;
        this.companyService = companyService;

        this.templateEngine.addDialect(new Java8TimeDialect());
    }

    public File generatePdf(Long id, String documentType) throws IOException, DocumentException {
        String html;
        Context context;
        Company company;

        if (documentType.equals("invoice")) {
            Invoice invoice = this.invoiceService.getById(id);
            company = this.companyService.findById(invoice.getCompany().getId());
            context = getInvoiceContext(invoice);
            html = loadAndFillTemplate(context, company.getCompanyTaxType());
            return renderPdf(html, invoice);
        } else {
            PriceOffer priceOffer = this.priceOfferService.getById(id);
            company = this.companyService.findById(priceOffer.getCompany().getId());
            context = getPriceOfferContext(priceOffer);
            html = loadAndFillTemplate(context, company.getCompanyTaxType());
            return renderPdf(html, priceOffer);
        }
    }

    private File renderPdf(String html, DocumentBase document) throws IOException, DocumentException {
        File file = File.createTempFile(document.getTitle(), ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer();

        renderer.getFontResolver().addFont("/fonts/OpenSans.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.getFontResolver().addFont("/fonts/DejaVuSans.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);

        outputStream.close();
        file.deleteOnExit();

        return file;
    }

    private Context getInvoiceContext(Invoice invoice) {
        Context context = getBasicContext(invoice);

        context.setVariable("headerComment", invoice.getHeaderComment());
        context.setVariable("paymentMethod", invoice.getPaymentMethod());
        context.setVariable("variableSymbol", invoice.getVariableSymbol());
        context.setVariable("subject", invoice.getSubject());
        context.setVariable("type", "I");
        context.setVariable("document", invoice);
        context.setVariable("payedValue", invoiceService.getInvoicePayedTaxesValuesMap(invoice.getPacks()));
        context.setVariable("payedTaxValue", invoiceService.getTaxPayedTaxesValuesMap(invoice.getPacks()));

        if (invoice.getDocumentType() == DocumentType.TAX_DOCUMENT) {
            context.setVariable("taxDocPayed", invoiceService.getTaxDocumentPayedValue(invoice.getPacks()));
        }

        return context;
    }

    private Context getPriceOfferContext(PriceOffer priceOffer) {
        Context context = getBasicContext(priceOffer);

        context.setVariable("headerComment", "");
        context.setVariable("type", "P");
        context.setVariable("document", priceOffer);
        context.setVariable("payedValue", new HashMap<Integer, BigDecimal>());

        return context;
    }

    private Context getBasicContext(DocumentBase document) {
        Company company = this.companyService.findById(document.getCompany().getId());
        HashMap<Integer, BigDecimal> taxesValuesMap = invoiceService.getTaxesValuesMap(invoiceService.getInvoiceItems(document.getPacks()));

        if (document.getDiscount().compareTo(BigDecimal.ZERO) == 1) {
            taxesValuesMap = sortHashmapAndSubtractDiscount(taxesValuesMap, document.getDiscount());
        }

        Context context = new Context();
        context.setVariable("firm", document.getDocumentData().get("firm"));
        context.setVariable("bankAccount", document.getDocumentData().get("bankAccount"));
        context.setVariable("taxes", taxesValuesMap);
        context.setVariable("user", document.getDocumentData().get("user"));
        context.setVariable("createdWeb", "");
        context.setVariable("note", document.getNote() != null ? document.getNote().strip() : "");
        context.setVariable("logo_url", company.getLogoUrl());
        context.setVariable("signature_url", company.getSignatureUrl());

        return context;
    }

    private String loadAndFillTemplate(Context context, CompanyTaxType type) {
        switch(type) {
            case NO_TAX_PAYER:
                return templateEngine.process("view/invoice/document_not_taxPayer", context);
            case TAX_PAYER:
            default:
                return templateEngine.process("view/invoice/document", context);
        }
    }

    private HashMap<Integer, BigDecimal> sortHashmapAndSubtractDiscount(HashMap<Integer, BigDecimal> originalMap, BigDecimal discount) {
        HashMap<Integer, BigDecimal> sorted = new HashMap<>();

        List<Integer> keys = new ArrayList<>(originalMap.keySet());
        Collections.sort(keys);
        Collections.reverse(keys);

        keys.forEach(key -> {
            if (discount.compareTo(BigDecimal.ZERO) == 1) {
                sorted.put(key, originalMap.get(key).subtract(originalMap.get(key).multiply(discount.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP))
                                .setScale(2, RoundingMode.HALF_UP)).setScale(2, RoundingMode.HALF_UP));
            } else {
                sorted.put(key, originalMap.get(key));
            }
        });

        return sorted;
    }
}