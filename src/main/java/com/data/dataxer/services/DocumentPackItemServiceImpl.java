package com.data.dataxer.services;

import com.data.dataxer.models.domain.DocumentPack;
import com.data.dataxer.models.domain.DocumentPackItem;
import com.data.dataxer.repositories.DocumentPackItemRepository;
import com.data.dataxer.repositories.DocumentPackRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentPackItemServiceImpl implements DocumentPackItemService {
    @Autowired
    DocumentPackRepository documentPackRepository;

    @Autowired
    DocumentPackItemRepository documentPackItemRepository;

    @Override
    public void deleteItemFromDocument(Long documentId, Long packId, Long packItemId) {
        Optional<DocumentPack> optionalDocumentPack = this.documentPackRepository.findByIdAndDocumentId(packId, documentId);

        if (optionalDocumentPack.isPresent()) {
            DocumentPack documentPack = optionalDocumentPack.get();

            DocumentPackItem documentPackItem = this
                    .documentPackItemRepository
                    .findByIdAndPackIdAndAppProfileId(packItemId, documentPack.getId(), SecurityUtils.defaultProfileId());

            this.documentPackItemRepository.delete(documentPackItem);
        }
    }
}
