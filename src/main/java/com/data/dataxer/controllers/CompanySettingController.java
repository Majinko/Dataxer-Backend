package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CompanySettingMapper;
import com.data.dataxer.models.dto.CompanySettingDTO;
import com.data.dataxer.services.CompanySettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/companySetting")
public class CompanySettingController {
    @Autowired
    CompanySettingService companySettingService;

    @Autowired
    CompanySettingMapper companySettingMapper;

    @PostMapping("/storeOrUpdate")
    public void storeOrUpdateByType(@RequestBody CompanySettingDTO companySettingDTO) {
        companySettingService.storeOrUpdate(this.companySettingMapper.companySettingDtoToCompanySetting(companySettingDTO));
    }
}
