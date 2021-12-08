package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentBase;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.domain.PriceOffer;
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
import java.util.HashMap;

@Service
public class PdfService {
    private static final String PDF_RESOURCES = "/templates/view/invoice/pdf-resources/";

    private final SpringTemplateEngine templateEngine;
    private final InvoiceService invoiceService;
    private final PriceOfferService priceOfferService;

    public PdfService(SpringTemplateEngine templateEngine, InvoiceService invoiceService,
                      PriceOfferService priceOfferService) {
        this.templateEngine = templateEngine;
        this.invoiceService = invoiceService;
        this.priceOfferService = priceOfferService;

        this.templateEngine.addDialect(new Java8TimeDialect());
    }

    public File generatePdf(Long id, String documentType) throws IOException, DocumentException {
        String html;
        Context context;

        if (documentType.equals("invoice")) {
            Invoice invoice = this.invoiceService.getById(id);
            context = getInvoiceContext(invoice);
            html = loadAndFillTemplate(context);
            return renderPdf(html, invoice);
        } else {
            PriceOffer priceOffer = this.priceOfferService.getById(id);
            context = getPriceOfferContext(priceOffer);
            html = loadAndFillTemplate(context);
            return renderPdf(html, priceOffer);
        }
    }

    private File renderPdf(String html, DocumentBase document) throws IOException, DocumentException {
        File file = File.createTempFile(document.getTitle(), ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 20);

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
        context.setVariable("payedValue", invoiceService.getPayedTaxesValuesMap(invoice.getPacks()));
        context.setVariable("payedTaxValue", invoiceService.getTaxPayedTaxesValuesMap(invoice.getPacks()));

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
        Context context = new Context();

        context.setVariable("firm", document.getDocumentData().get("firm"));
        context.setVariable("bankAccount", document.getDocumentData().get("bankAccount"));
        context.setVariable("taxes", invoiceService.getTaxesValuesMap(invoiceService.getInvoiceItems(document.getPacks())));
        context.setVariable("user", document.getDocumentData().get("user"));
        context.setVariable("createdWeb", "");

        return context;
    }

    private String loadAndFillTemplate(Context context) {
        return templateEngine.process("view/invoice/document", context);
    }
}