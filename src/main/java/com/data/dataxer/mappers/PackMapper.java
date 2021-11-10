package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.ItemPrice;
import com.data.dataxer.models.domain.Pack;
import com.data.dataxer.models.domain.PackItem;
import com.data.dataxer.models.dto.ItemPriceDTO;
import com.data.dataxer.models.dto.PackDTO;
import com.data.dataxer.models.dto.PackItemDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface PackMapper {
    Pack packDTOtoPack(PackDTO packDTO);

    @Named(value = "packToPackDTO")
    PackDTO packToPackDTO(Pack pack);

    @Named(value = "packToPackDTOSimple")
    @Mapping(target = "packItems", ignore = true)
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
    @Mapping(target = "item.categories", ignore = true)
    @Mapping(target = "item.supplier", ignore = true)
    @Mapping(target = "item.files", ignore = true)
    PackItemDTO packItemToPackItemDTO(PackItem packItem);

    @IterableMapping(qualifiedByName = "packToPackDTOSimple")
    List<PackDTO> packToPackDTOsSimple(List<Pack> packItems);
}
