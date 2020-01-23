package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.models.dto.DataxerUserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    DataxerUserDTO toDataxerUserDTO(DataxerUser user);

    DataxerUser toDataxerUser(DataxerUserDTO userDTO);
}
