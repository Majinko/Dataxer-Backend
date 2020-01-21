package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.dto.CategoryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    List<CategoryDTO> toCategoryDTOs(List<Category> categories);

    CategoryDTO toCategoryDTO(Category category);

    @Mapping(target = "updatedAt", source = "")
    @Mapping(target = "updated", source = "")
    @Mapping(target = "parent", source = "")
    @Mapping(target = "deletedAt", source = "")
    @Mapping(target = "createdAt", source = "")
    @Mapping(target = "created", source = "")
    @Mapping(target = "company", source = "")
    @Mapping(target = "child", source = "")
    Category toCategory(CategoryDTO categoryDTO);
}

