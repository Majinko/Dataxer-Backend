package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Cost;
import com.data.dataxer.models.dto.CostDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CostMapper {

    Cost costDTOToCost(CostDTO costDTO);

    CostDTO costToCostDTO(Cost cost);

}
