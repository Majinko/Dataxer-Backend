package com.data.dataxer.mappers;

import com.data.dataxer.models.dto.RoleDTO;
import com.data.dataxer.security.model.Role;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {
    Role roleDTOtoRole(RoleDTO roleDTO);

    RoleDTO roleToRoleDTO(Role role);

    List<RoleDTO> rolesToRoleDTOs(List<Role> roles);
}
