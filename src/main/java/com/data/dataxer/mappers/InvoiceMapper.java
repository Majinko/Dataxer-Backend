package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.DocumentPackItem;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.dto.DocumentPackItemDTO;
import com.data.dataxer.models.dto.InvoiceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface InvoiceMapper {
    Invoice invoiceDTOtoInvoice(InvoiceDTO invoiceDTO);

    InvoiceDTO invoiceToInvoiceDTO(Invoice invoice);

    @Mapping(target = "packs", ignore = true)
    InvoiceDTO invoiceToInvoiceDTOSimple(Invoice invoice);

    @Mapping(target = "item.category", ignore = true)
    @Mapping(target = "item.supplier", ignore = true)
    DocumentPackItemDTO documentPackItemToDocumentPackItemDTO(DocumentPackItem documentPackItem);

}
