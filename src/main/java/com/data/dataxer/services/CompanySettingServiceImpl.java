package com.data.dataxer.services;

import com.data.dataxer.models.domain.CompanySetting;
import com.data.dataxer.repositories.CompanySettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanySettingServiceImpl implements CompanySettingService {
    @Autowired
    CompanySettingRepository companySettingRepository;

    @Override
    public void storeOrUpdate(CompanySetting companySetting) {
        this.companySettingRepository.save(companySetting);
    }
}
