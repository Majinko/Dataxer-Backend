package com.data.dataxer.mappers;

import com.data.dataxer.models.dto.RoleDTO;
import com.data.dataxer.security.model.Role;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface RoleMapper {
    Role roleDTOtoRole(RoleDTO roleDTO);

    RoleDTO roleToRoleDTO(Role role);

    @Named(value = "roleToRoleDTOSimple")
    @Mapping(target = "privileges", ignore = true)
    RoleDTO roleToRoleDTOSimple(Role role);

    List<RoleDTO> rolesToRoleDTOs(List<Role> roles);

    List<Role> roleDTOStoRoles(List<RoleDTO> roleDTOS);

    @IterableMapping(qualifiedByName = "roleToRoleDTOSimple")
    List<RoleDTO> rolesToRoleDTOsSimple(List<Role> roles);
}
