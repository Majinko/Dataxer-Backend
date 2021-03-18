package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.models.dto.CategoryNestedDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CategoryMapper {
    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    List<CategoryDTO> toCategoryDTOs(List<Category> categories);

    List<Category> toCategories(List<CategoryDTO> categoryDTOS);

    List<Category> CategoryNestedDTOsToCategories(List<CategoryNestedDTO> categoryDTOS);

    @Mapping(target = "parent", ignore = true)
    CategoryDTO toCategoryDTO(Category category);

    CategoryNestedDTO toCategoryNestedDTO(Category category);

    List<CategoryNestedDTO> toCategoryNestedDTOs(List<Category> categories);

    //@Mapping(target = "rgt", source = "")
    //@Mapping(target = "lft", source = "")
    //@Mapping(target = "deletedAt", source = "")
    //@Mapping(target = "createdAt", source = "")
    //@Mapping(target = "company", source = "")
    //@Mapping(target = "children", source = "")
    Category toCategory(CategoryDTO categoryDTO);
}

