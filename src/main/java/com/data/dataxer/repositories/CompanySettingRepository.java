package com.data.dataxer.repositories;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.CompanySetting;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CompanySettingRepository extends CrudRepository<CompanySetting, Long> {
}
