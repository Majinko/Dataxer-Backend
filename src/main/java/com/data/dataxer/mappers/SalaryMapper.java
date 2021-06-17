package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Salary;
import com.data.dataxer.models.dto.SalaryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface SalaryMapper {
    Salary salaryDTOtoSalary(SalaryDTO salaryDTO);

    @Mapping(target = "user", ignore = true)
    SalaryDTO salaryToSalaryDTO(Salary salary);

    List<SalaryDTO> salariesToSalariesDto(List<Salary> salaries);
}
