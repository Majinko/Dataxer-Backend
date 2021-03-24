package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Category;
import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.models.dto.CostDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface CostMapper {
    Cost costDTOToCost(CostDTO costDTO);

    @Named("cosToCostDTOWithoutRelations")
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "contact", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "files", ignore = true)
    CostDTO cosToCostDTOWithoutRelations(Cost cost);

    @Mapping(target = "project.categories", ignore = true)
    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "categories.parent", ignore = true)
    CostDTO costToCostDTO(Cost cost);

    @Mapping(target = "files", ignore = true)
    @Mapping(target = "project.categories", ignore = true)
    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "categories", ignore = true)
    CostDTO costToCostDTOPaginate(Cost cost);

    @IterableMapping(qualifiedByName = "cosToCostDTOWithoutRelations")
    List<CostDTO> costsToCostDTOsWithoutRelations(List<Cost> costs);

    @Mapping(target = "parent", ignore = true)
    CategoryDTO categoryToCategoryDto(Category category);
}
