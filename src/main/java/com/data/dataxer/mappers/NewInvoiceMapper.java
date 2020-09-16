package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.DocumentPackItem;
import com.data.dataxer.models.domain.NewInvoice;
import com.data.dataxer.models.dto.DocumentPackItemDTO;
import com.data.dataxer.models.dto.InvoiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface NewInvoiceMapper {

    NewInvoice invoiceDTOToNewInvoice(InvoiceDTO invoiceDTO);

    InvoiceDTO newInvoiceToInvoiceDTO(NewInvoice newInvoice);

    @Mapping(target = "packs", ignore = true)
    InvoiceDTO newInvoiceToInvoiceDTOSimple(NewInvoice newInvoice);

    @Mapping(target = "item.category", ignore = true)
    @Mapping(target = "item.supplier", ignore = true)
    DocumentPackItemDTO documentPackItemToDocumentPackItemDTO(DocumentPackItem documentPackItem);

}
