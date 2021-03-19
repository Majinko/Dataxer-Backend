package com.data.dataxer.mappers;

import com.data.dataxer.models.dto.PrivilegeDTO;
import com.data.dataxer.security.model.Privilege;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface PrivilegeMapper {

    List<Privilege> privilegeDTOSToPrivileges(List<PrivilegeDTO> privilegeDTOS);

    List<PrivilegeDTO> privilegesToPrivilegeDTOS(List<Privilege> privileges);

}
