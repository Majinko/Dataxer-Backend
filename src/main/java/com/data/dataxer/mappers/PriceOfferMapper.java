package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.PriceOffer;
import com.data.dataxer.models.dto.PriceOfferDTO;
import org.mapstruct.Mapper;

@Mapper
public interface PriceOfferMapper {
    PriceOffer priceOfferDTOtoPriceOffer(PriceOfferDTO priceOfferDTO);

    PriceOfferDTO priceOfferToPriceOfferDTO(PriceOffer priceOffer);
}
