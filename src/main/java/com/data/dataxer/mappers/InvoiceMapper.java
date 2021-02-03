package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.DocumentPack;
import com.data.dataxer.models.domain.DocumentPackItem;
import com.data.dataxer.models.domain.Invoice;
import com.data.dataxer.models.dto.DocumentPackItemDTO;
import com.data.dataxer.models.dto.InvoiceDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface InvoiceMapper {
    Invoice invoiceDTOtoInvoice(InvoiceDTO invoiceDTO);

    @Named(value = "invoiceToInvoiceDTO")
    InvoiceDTO invoiceToInvoiceDTO(Invoice invoice);

    @Mapping(target = "packs", ignore = true)
    InvoiceDTO invoiceToInvoiceDTOSimple(Invoice invoice);


    @Mapping(target = "packs", ignore = true)
    @Mapping(target = "contact", ignore = true)
    @Named(value = "invoiceToInvoiceDtoWithoutRelation")
    InvoiceDTO invoiceToInvoiceDtoWithoutRelation(Invoice invoice);

    @Named(value = "invoiceToInvoiceDtoWithoutRelation")
    List<InvoiceDTO> invoicesToInvoicesDTOWithoutRelation(List<Invoice> invoices);

    @Mapping(target = "item.category", ignore = true)
    @Mapping(target = "item.supplier", ignore = true)
    DocumentPackItemDTO documentPackItemToDocumentPackItemDTO(DocumentPackItem documentPackItem);
}
