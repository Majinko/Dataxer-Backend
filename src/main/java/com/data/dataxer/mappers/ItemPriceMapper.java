package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.ItemPrice;
import com.data.dataxer.models.dto.ItemPriceDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ItemPriceMapper {
    ItemPrice toItemPrice(ItemPriceDTO itemPriceDTO);

    ItemPriceDTO toItemPriceDto(ItemPrice itemPrice);
}
