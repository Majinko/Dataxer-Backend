package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Item;
import com.data.dataxer.models.dto.ItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ItemMapper {
    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

    ItemDTO toItemDto(Item item);

    @Mapping(target = "updatedAt", source = "")
    @Mapping(target = "deletedAt", source = "")
    @Mapping(target = "createdAt", source = "")
    Item toItem(ItemDTO itemDTO);
}
