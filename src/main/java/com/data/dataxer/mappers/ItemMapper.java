package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Item;
import com.data.dataxer.models.domain.ItemPrice;
import com.data.dataxer.models.dto.ItemDTO;
import com.data.dataxer.models.dto.ItemPriceDTO;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ItemMapper {
    ItemPriceDTO toItemPriceDto(ItemPrice itemPrice);

    @Mapping(target = "itemPrice", expression = "java(toItemPriceDto(!item.getItemPrices().isEmpty() ? item.getItemPrices().get(0) : null))")
    @Mapping(target = "category.parent", ignore = true)
    ItemDTO itemToItemDto(Item item);

    @Mapping(target = "category.parent", ignore = true)
    Item toItem(ItemDTO itemDTO);

    @Named(value = "itemToItemDTOWithPrice")
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "itemPrice", expression = "java(toItemPriceDto(!item.getItemPrices().isEmpty() ? item.getItemPrices().get(0) : null))")
    ItemDTO itemToItemDTOWithPrice(Item item);

    @Named(value = "useWithoutPrice")
    @Mapping(target = "itemPrice", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    ItemDTO itemToItemDtoSimple(Item item);


    @IterableMapping(qualifiedByName = "itemToItemDTOWithPrice")
    List<ItemDTO> itemsToItemsDTOsWithPrice(List<Item> items);
    
    /*@AfterMapping
    default void set(@MappingTarget ItemDTO itemDTO, Item item) {
        itemDTO.setItemPrice(toItemPriceDto(!item.getItemPrices().isEmpty() ? item.getItemPrices().get(0) : null));
    }*/
}
