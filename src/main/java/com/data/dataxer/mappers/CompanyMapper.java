package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.dto.CompanyDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CompanyMapper {
    CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

    @Named(value = "useMe")
    @Mapping(target = "billingInformation", ignore = true)
    CompanyDTO toCompanyDTO(Company company);

    CompanyDTO toCompanyWithBillingInfoDTO(Company company);

    Company toCompany(CompanyDTO companyDTO);

    Company toCompanyWithBillingInfo(CompanyDTO company);

    @IterableMapping(qualifiedByName = "useMe")
    List<CompanyDTO> toCompaniesDTO(List<Company> companies);
}
