package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.AppProfile;
import com.data.dataxer.models.dto.AppProfileDTO;
import org.mapstruct.Mapper;

@Mapper
public interface AppProfileMapper {
    AppProfileDTO appProfileToAppProfileDTO(AppProfile appProfile);
}
