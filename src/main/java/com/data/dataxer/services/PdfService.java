package com.data.dataxer.services;

import com.lowagie.text.DocumentException;

import java.io.File;
import java.io.IOException;

public interface PdfService {

    File generatePdf(Long id) throws IOException, DocumentException;

}
