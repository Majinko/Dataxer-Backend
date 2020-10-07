package com.data.dataxer.services;

import com.data.dataxer.models.domain.Invoice;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.pdf.BaseFont;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;
import com.lowagie.text.DocumentException;

import org.thymeleaf.context.Context;
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
    }

    public File generatePdf() throws IOException, DocumentException {
        Context context = getContext();
        String html = loadAndFillTemplate(context);
        return renderPdf(html);
    }

    private File renderPdf(String html) throws IOException, DocumentException {
        File file = File.createTempFile("students", ".pdf");
        OutputStream outputStream = new FileOutputStream(file);
        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 20);
        renderer.getFontResolver().addFont("/fonts/OpenSans.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
        renderer.setDocumentFromString(html, new ClassPathResource(PDF_RESOURCES).getURL().toExternalForm());
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();
        file.deleteOnExit();
        return file;
    }

    private Context getContext() {
        Invoice invoice = this.invoiceService.getById(3L);

        Context context = new Context();
        context.setVariable("invoice", invoice);
        return context;
    }

    private String loadAndFillTemplate(Context context) {
        return templateEngine.process("view/invoice/pdf", context);
    }
}
