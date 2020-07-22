package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.dto.AppUserDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    AppUserDTO appUserToAppUserDTO(AppUser user);

    @Mapping(target = "updatedAt", source = "")
    @Mapping(target = "roles", source = "")
    @Mapping(target = "deletedAt", source = "")
    @Mapping(target = "createdAt", source = "")
    AppUser appUserDTOtoAppUser(AppUserDTO userDTO);

    @Named(value = "appUserToAppUserDTOSimple")
    @Mapping(target = "companies", ignore = true)
    AppUserDTO appUserToAppUserDTOSimple(AppUser appUser);

    @IterableMapping(qualifiedByName = "appUserToAppUserDTOSimple")
    List<AppUserDTO> appUserToAppUserDTOs(List<AppUser> appUsers);
}
