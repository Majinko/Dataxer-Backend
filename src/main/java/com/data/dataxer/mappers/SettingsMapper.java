package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Settings;
import com.data.dataxer.models.dto.SettingsDTO;
import org.mapstruct.Mapper;

@Mapper
public interface SettingsMapper {

    Settings settingsDTOToSettings(SettingsDTO settingsDTO);

    SettingsDTO settingsToSettingsDTO(Settings settings);

}
