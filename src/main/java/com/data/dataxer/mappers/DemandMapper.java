package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Demand;
import com.data.dataxer.models.domain.DemandPackItem;
import com.data.dataxer.models.dto.DemandDTO;
import com.data.dataxer.models.dto.DemandPackItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface DemandMapper {
    Demand demandDTOtoDemand(DemandDTO demandDTO);

    @Named("demandToDemandDTO")
    @Mapping(target = "project.categories", ignore = true)
    @Mapping(target = "project.contact", ignore = true)
    DemandDTO demandToDemandDTO(Demand demand);

    @Mapping(target = "packs", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    DemandDTO demandToDemandPaginateDTO(Demand demand);

    @Named(value = "demandToDemandDTOSimple")
    DemandDTO demandToDemandDTOSimple(Demand demand);

    @Mapping(target = "item.files", ignore = true)
    @Mapping(target = "item.supplier", ignore = true)
    @Mapping(target = "item.category", ignore = true)
    DemandPackItemDTO demandPackItemToDemandPackItemDTO(DemandPackItem demandPackItem);
}
