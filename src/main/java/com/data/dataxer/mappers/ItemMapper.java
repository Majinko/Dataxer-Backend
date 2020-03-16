package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Item;
import com.data.dataxer.models.dto.ItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    @Mapping(target = "wholesaleTax", source = "")
    @Mapping(target = "wholesalePrice", source = "")
    @Mapping(target = "tax", source = "")
    @Mapping(target = "price", source = "")
    ItemDTO toItemDto(Item item);

    @Mapping(target = "updatedAt", source = "")
    @Mapping(target = "updated", source = "")
    @Mapping(target = "itemPrices", source = "")
    @Mapping(target = "deletedAt", source = "")
    @Mapping(target = "createdAt", source = "")
    @Mapping(target = "created", source = "")
    @Mapping(target = "company", source = "")
    Item toItem(ItemDTO itemDTO);
}
