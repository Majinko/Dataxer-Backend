package com.data.dataxer.services;

import com.data.dataxer.models.domain.BillingInformation;
import com.data.dataxer.models.domain.Company;

import java.util.List;

public interface BillingInformationServiceImpl {
    void store(Company company, List<BillingInformation> billingInformation);

    void update(List<BillingInformation> billingInformation);
}
