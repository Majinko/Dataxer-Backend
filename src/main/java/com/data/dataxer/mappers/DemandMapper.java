package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.models.dto.DemandDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DemandMapper {
    Demand demandDTOtoDemand(DemandDTO demandDTO);

    DemandDTO demandToDemandDTO(Demand demand);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "contact", ignore = true)
    DemandDTO demandToDemandDTOSimple(Demand demand);
}
