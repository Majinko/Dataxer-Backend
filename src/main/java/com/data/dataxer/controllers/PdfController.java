package com.data.dataxer.controllers;

import com.data.dataxer.services.InvoiceService;
import com.data.dataxer.services.PdfService;
import com.lowagie.text.DocumentException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/api/pdf")
@PreAuthorize("hasPermission(null, 'Pdf', 'Pdf')")
public class PdfController {
    private final PdfService pdfService;
    private final InvoiceService invoiceService;

    public PdfController(PdfService pdfService, InvoiceService invoiceService) {
        this.pdfService = pdfService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/pdf")
    public ModelAndView pdf(ModelAndView modelAndView) {
        modelAndView.addObject("invoice", this.invoiceService.getById(4L));
        modelAndView.setViewName("view/invoice/show");
        return modelAndView;
    }

    @RequestMapping(value = "/download-pdf/{id}", method = RequestMethod.GET)
    public void downloadPDFResource(@PathVariable Long id, HttpServletResponse response) {
        try {
            Path file = Paths.get(pdfService.generatePdf(id).getAbsolutePath());

            if (Files.exists(file)) {
                response.setContentType("application/pdf");
                response.addHeader("Content-Disposition", "inline; filename=" + file.getFileName());
                Files.copy(file, response.getOutputStream());
                response.getOutputStream().flush();
            }
        } catch (DocumentException | IOException ex) {
            ex.printStackTrace();
        }
    }
}
