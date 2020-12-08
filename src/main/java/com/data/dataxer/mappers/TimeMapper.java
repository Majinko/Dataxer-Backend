package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Time;
import com.data.dataxer.models.dto.TimeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface TimeMapper {

    Time timeDTOToTime(TimeDTO timeDTO);

    @Mapping(target = "project.contact", ignore = true)
    @Mapping(target = "user.companies", ignore = true)
    @Mapping(target = "user.roles", ignore = true)
    TimeDTO timeToTimeDTO(Time time);

    List<TimeDTO> timeListToTimeDTOList(List<Time> times);
}
