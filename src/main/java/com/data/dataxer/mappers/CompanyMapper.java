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

    @Mapping(target = "updatedAt", source = "")
    @Mapping(target = "updated", source = "")
    @Mapping(target = "createdAt", source = "")
    @Mapping(target = "created", source = "")
    @Mapping(target = "company", source = "")
    Company toCompany(CompanyDTO companyDTO);
}
