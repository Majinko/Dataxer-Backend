package com.data.dataxer.services;

import com.data.dataxer.models.domain.Invoice;
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

@Service
public class PdfService {
    private static final String PDF_RESOURCES = "/templates/view/invoice/pdf-resources/";

    private SpringTemplateEngine templateEngine;
    private final InvoiceService invoiceService;

    public PdfService(SpringTemplateEngine templateEngine, InvoiceService invoiceService) {
        this.templateEngine = templateEngine;
        this.invoiceService = invoiceService;

        this.templateEngine.addDialect(new Java8TimeDialect());
    }

    public File generatePdf(Invoice invoice) throws IOException, DocumentException {
        Context context = getContext(invoice);
        String html = loadAndFillTemplate(context);

        return renderPdf(html, invoice);
    }

    private File renderPdf(String html, Invoice invoice) throws IOException, DocumentException {
        File file = File.createTempFile(invoice.getTitle(), ".pdf");
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

    private Context getContext(Invoice invoice) {
        Context context = new Context();

        context.setVariable("firm", invoice.getDocumentData().get("firm"));
        context.setVariable("bankAccount", invoice.getDocumentData().get("bankAccount"));
        context.setVariable("taxes", invoiceService.getTaxesValuesMap(invoiceService.getInvoiceItems(invoice.getPacks())));
        context.setVariable("invoice", invoice);
        context.setVariable("createdName", "Janko Hrasko");
        context.setVariable("createdPhone", "0905123456");
        context.setVariable("createdWeb", "www.example.com");
        context.setVariable("createdEmail", "janko.hrasko@example.com");

        return context;
    }

    private String loadAndFillTemplate(Context context) {
        return templateEngine.process("view/invoice/invoice_template", context);
    }
}
