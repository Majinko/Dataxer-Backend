package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.CompanySettingType;
import lombok.Data;

import java.util.Map;

@Data
public class CompanySettingDTO {
    private Long id;
    private CompanySettingType companySettingType;
    protected Map<String, Object> data;
}
