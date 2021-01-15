package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.dto.CostDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CostMapper {

    Cost costDTOToCost(CostDTO costDTO);

    CostDTO costToCostDTO(Cost cost);

    @Mapping(target = "files", ignore = true)
    CostDTO costToCostDTOPaginate(Cost cost);
}
