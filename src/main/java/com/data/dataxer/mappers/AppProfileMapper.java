package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.AppProfile;
import com.data.dataxer.models.dto.AppProfileDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface AppProfileMapper {
    AppProfile appProfileDTOtoAppProfile(AppProfileDTO appProfileDTO);

    AppProfileDTO appProfileToAppProfileDTO(AppProfile appProfile);

    List<AppProfileDTO> appProfilesToAppProfileDTOs(List<AppProfile> appProfiles);
}
