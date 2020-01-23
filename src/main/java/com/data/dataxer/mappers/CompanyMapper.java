package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.dto.CompanyDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    CompanyDTO toCompanyDTO(Company company);

    Company toCompany(CompanyDTO companyDTO);
}
