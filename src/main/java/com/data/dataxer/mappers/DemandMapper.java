package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.models.dto.DemandDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface DemandMapper {
    Demand demandDTOtoDemand(DemandDTO demandDTO);

    DemandDTO demandToDemandDTO(Demand demand);

    @Named(value = "demandToDemandDTOSimple")
    DemandDTO demandToDemandDTOSimple(Demand demand);
}
