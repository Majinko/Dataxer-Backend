package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Time;
import com.data.dataxer.models.dto.TimeDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface TimeMapper {

    Time timeDTOToTime(TimeDTO timeDTO);

    TimeDTO timeToTimeDTO(Time time);

    List<TimeDTO> timeListToTimeDTOList(List<Time> times);

}
