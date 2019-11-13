package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.User;
import com.data.dataxer.models.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDTO toUserDto(User user);

    @Mapping(target = "updatedAt", source = "")
    @Mapping(target = "deletedAt", source = "")
    @Mapping(target = "createdAt", source = "")
    User toUser(UserDTO userDTO);
}
