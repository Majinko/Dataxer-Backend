package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Pack;
import com.data.dataxer.models.domain.PackItem;
import com.data.dataxer.models.domain.ItemPrice;
import com.data.dataxer.models.dto.PackDTO;
import com.data.dataxer.models.dto.PackItemDTO;
import com.data.dataxer.models.dto.ItemPriceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PackMapper {
    PackMapper INSTANCE = Mappers.getMapper(PackMapper.class);

    Pack packDTOtoPack(PackDTO packDTO);

    PackDTO packToPackDTO(Pack pack);

    @Mapping(target = "items", ignore = true)
    PackDTO packToPackDTOSimple(Pack pack);

    @Mapping(target = "wholesalePrice", ignore = true)
    @Mapping(target = "wholesaleTax", ignore = true)
    @Mapping(target = "marge", ignore = true)
    @Mapping(target = "surcharge", ignore = true)
    ItemPriceDTO toItemPriceDto(ItemPrice itemPrice);

    @Mapping(
            target = "item.itemPrice",
            expression = "java(toItemPriceDto(!item.getItemPrices().isEmpty() ? item.getItemPrices().get(0) : null))"
    )
    @Mapping(target = "item.category", ignore = true)
    @Mapping(target = "item.supplier", ignore = true)
    PackItemDTO packItemToPackItemDTO(PackItem packItem);
}
