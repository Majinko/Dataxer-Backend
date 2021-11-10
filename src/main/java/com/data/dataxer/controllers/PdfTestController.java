package com.data.dataxer.controllers;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

@Controller
@RequestMapping("/PdfTestController")
public class PdfTestController {
    @Value("${file.upload-dir}")
    private String uploadDirectory;

    @GetMapping
    public @ResponseBody
    String generate() {
        try {
            PDDocument document = PDDocument.load(new java.io.File(this.uploadDirectory + "/test.pdf"));
            PDAcroForm pDAcroForm = document.getDocumentCatalog().getAcroForm();

            PDField pdField = pDAcroForm.getField("logo_image");

            PDRectangle rectangle = getFieldArea(pdField);
            float size = rectangle.getHeight();
            float x = rectangle.getLowerLeftX();
            float y = rectangle.getLowerLeftY();

            PDImageXObject pdImage = PDImageXObject.createFromFileByContent(new java.io.File(this.uploadDirectory + "/logo-dark_sk.png"), document);

            PDPageContentStream contentStream = new PDPageContentStream(document, document.getPage(0), PDPageContentStream.AppendMode.APPEND, true);
            contentStream.drawImage(pdImage, x, y, size, size);
            contentStream.close();

            pdField.setValue("Off");

            document.save((new java.io.File(this.uploadDirectory + "/generate.pdf")));
            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return "hotovo";
    }


    private PDRectangle getFieldArea(PDField field) {
        COSDictionary fieldDict = field.getCOSObject();
        COSArray fieldAreaArray = (COSArray) fieldDict.getDictionaryObject(COSName.RECT);
        return new PDRectangle(fieldAreaArray);
    }
}
