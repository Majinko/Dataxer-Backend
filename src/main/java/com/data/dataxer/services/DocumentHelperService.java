package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentBase;
import com.data.dataxer.models.domain.DocumentPack;
import com.data.dataxer.models.domain.DocumentPackItem;
import com.data.dataxer.models.enums.DocumentType;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

public class DocumentHelperService {
    protected DocumentBase setDocumentPackAndItems(DocumentBase documentBase) {
        int packPosition = 0;

        for (DocumentPack documentPack : documentBase.getPacks()) {
            documentPack.setDocument(documentBase);
            documentPack.setType(DocumentType.INVOICE);
            documentPack.setPosition(packPosition);
            packPosition++;

            int packItemPosition = 0;

            for (DocumentPackItem packItem : documentPack.getPackItems()) {
                packItem.setPack(documentPack);
                packItem.setPosition(packItemPosition);

                packItemPosition++;
            }
        }

        return documentBase;
    }

    protected List<DocumentPack> duplicateDocumentPacks(List<DocumentPack> originalDocumentPacks) {
        List<DocumentPack> duplicatedDocumentPacks = new ArrayList<>();
        for (DocumentPack originalDocumentPack : originalDocumentPacks) {
            DocumentPack duplicatePack = new DocumentPack();
            BeanUtils.copyProperties(originalDocumentPack, duplicatePack, "id", "packItems");
            duplicatePack.setPackItems(this.duplicateDocumentPackItems(originalDocumentPack.getPackItems()));
            duplicatedDocumentPacks.add(duplicatePack);
        }
        return duplicatedDocumentPacks;
    }

    protected List<DocumentPackItem> duplicateDocumentPackItems(List<DocumentPackItem> originalPackItems) {
        List<DocumentPackItem> duplicatedDocumentPackItems = new ArrayList<>();
        for (DocumentPackItem originalDocumentPackItem : originalPackItems) {
            DocumentPackItem duplicatedDocumentPackItem = new DocumentPackItem();
            BeanUtils.copyProperties(originalDocumentPackItem, duplicatedDocumentPackItem, "id", "pack");
            duplicatedDocumentPackItems.add(duplicatedDocumentPackItem);
        }
        return duplicatedDocumentPackItems;
    }
}
