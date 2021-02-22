package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.dto.AppUserDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    AppUserDTO appUserToAppUserDTO(AppUser user);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    AppUser appUserDTOtoAppUser(AppUserDTO userDTO);

    @Named(value = "appUserToAppUserDTOSimple")
    @Mapping(target = "roles", ignore = true)
    AppUserDTO appUserToAppUserDTOSimple(AppUser appUser);

    @IterableMapping(qualifiedByName = "appUserToAppUserDTOSimple")
    List<AppUserDTO> appUserToAppUserDTOs(List<AppUser> appUsers);
}
