package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.DocumentPackItem;
import com.data.dataxer.models.domain.PriceOffer;
import com.data.dataxer.models.dto.DocumentPackItemDTO;
import com.data.dataxer.models.dto.PriceOfferDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface PriceOfferMapper {
    PriceOffer priceOfferDTOtoPriceOfferWithCompany(PriceOfferDTO priceOfferDTO);

    @Mapping(target = "project.categories", ignore = true)
    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "item.files", ignore = true)
    PriceOfferDTO priceOfferToPriceOfferDTOWithCompany(PriceOffer priceOffer);

    @Mapping(target = "company", ignore = true)
    PriceOffer priceOfferDTOtoPriceOffer(PriceOfferDTO priceOfferDTO);

    @Mapping(target = "project.categories", ignore = true)
    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "item.files", ignore = true)
    @Mapping(target = "company", ignore = true)
    PriceOfferDTO priceOfferToPriceOfferDTO(PriceOffer priceOffer);

    @Mapping(target = "packs", ignore = true)
    @Mapping(target = "contact", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Named(value = "priceOfferToPriceOfferDTOWithoutRelation")
    PriceOfferDTO priceOfferToPriceOfferDTOWithoutRelation(PriceOffer priceOffer);

    @Mapping(target = "packs", ignore = true)
    @Mapping(target = "project.categories", ignore = true)
    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "company", ignore = true)
    PriceOfferDTO priceOfferToPriceOfferDTOSimple(PriceOffer priceOffer);

    @Mapping(target = "item.category", ignore = true)
    @Mapping(target = "item.supplier", ignore = true)
    @Mapping(target = "item.files", ignore = true)
    @Mapping(target = "project.categories", ignore = true)
    DocumentPackItemDTO documentPackItemToDocumentPackItemDTO(DocumentPackItem documentPackItem);

    @IterableMapping(qualifiedByName = "priceOfferToPriceOfferDTOWithoutRelation")
    List<PriceOfferDTO> priceOffersToPriceOfferDTOsWithoutRelation(List<PriceOffer> priceOffers);
 }
