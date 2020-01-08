package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.models.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toUserDto(DataxerUser user);

    @Mapping(target = "roles", source = "")
    @Mapping(target = "updatedAt", source = "")
    @Mapping(target = "deletedAt", source = "")
    @Mapping(target = "createdAt", source = "")
    DataxerUser toUser(UserDTO userDTO);
}
