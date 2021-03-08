package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.dto.CostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CostMapper {

    Cost costDTOToCost(CostDTO costDTO);

    @Mapping(target = "project.categories", ignore = true)
    CostDTO costToCostDTO(Cost cost);

    @Mapping(target = "files", ignore = true)
    @Mapping(target = "project.categories", ignore = true)
    CostDTO costToCostDTOPaginate(Cost cost);
}
