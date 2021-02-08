package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Salary;
import com.data.dataxer.models.dto.SalaryDTO;
import org.mapstruct.Mapper;

@Mapper
public interface SalaryMapper {
    Salary salaryDTOtoSalary(SalaryDTO salaryDTO);

    SalaryDTO salaryToSalaryDTO(Salary salary);
}
