package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.DocumentPackItem;
import com.data.dataxer.models.domain.PriceOffer;
import com.data.dataxer.models.dto.DocumentPackItemDTO;
import com.data.dataxer.models.dto.PriceOfferDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PriceOfferMapper {
    PriceOffer priceOfferDTOtoPriceOffer(PriceOfferDTO priceOfferDTO);

    PriceOfferDTO priceOfferToPriceOfferDTO(PriceOffer priceOffer);

    @Mapping(target = "packs", ignore = true)
    @Mapping(target = "project.categories", ignore = true)
    PriceOfferDTO priceOfferToPriceOfferDTOSimple(PriceOffer priceOffer);

    @Mapping(target = "item.category", ignore = true)
    @Mapping(target = "item.supplier", ignore = true)
    DocumentPackItemDTO documentPackItemToDocumentPackItemDTO(DocumentPackItem documentPackItem);
}
