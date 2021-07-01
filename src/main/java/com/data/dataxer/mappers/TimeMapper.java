package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Time;
import com.data.dataxer.models.dto.TimeDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface TimeMapper {
    @Mapping(target = "project.categories", ignore = true)
    Time timeDTOToTime(TimeDTO timeDTO);

    @Named("timeToTimeDTO")
    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "project.categories", ignore = true)
    @Mapping(target = "user.roles", ignore = true)
    @Mapping(target = "user.defaultCompany", ignore = true)
    TimeDTO timeToTimeDTO(Time time);

    @Named("timeToTimeDTOSimple")
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "user.roles", ignore = true)
    @Mapping(target = "user.defaultCompany", ignore = true)
    TimeDTO timeToTimeDTOSimple(Time time);

    @Named("timeToTimeDTOWithoutRelations")
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "user.roles", ignore = true)
    @Mapping(target = "user.defaultCompany", ignore = true)
    @Mapping(target = "category", ignore = true)
    TimeDTO timeToTimeDTOWithoutRelations(Time time);


    @Mapping(target = "project", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    TimeDTO timeToTimeDTOWithoutRelation(Time time);

    @IterableMapping(qualifiedByName = "timeToTimeDTO")
    List<TimeDTO> timeListToTimeDTOList(List<Time> times);

    @IterableMapping(qualifiedByName = "timeToTimeDTOSimple")
    List<TimeDTO> timeListToTimeDTOListSimple(List<Time> times);

    @IterableMapping(qualifiedByName = "timeToTimeDTOWithoutRelations")
    List<TimeDTO> timeListToTimeDTOWithoutRelations(List<Time> times);
}
