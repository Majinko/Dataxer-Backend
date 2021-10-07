package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.CompanySetting;
import com.data.dataxer.models.dto.CompanySettingDTO;
import org.mapstruct.Mapper;

@Mapper
public interface CompanySettingMapper {
    CompanySetting companySettingDtoToCompanySetting(CompanySettingDTO companySettingDTO);

    CompanySettingDTO companySettingToCompanySettingDTO(CompanySetting companySetting);
}
