package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.models.dto.CategoryNestedDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<CategoryDTO> toCategoryDTOs(List<Category> categories);

    List<Category> toCategories(List<CategoryDTO> categoryDTOS);

    List<Category> CategoryNestedDTOsToCategories(List<CategoryNestedDTO> categoryDTOS);
}

