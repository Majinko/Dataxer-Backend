package com.data.dataxer.services;

import com.data.dataxer.models.domain.BillingInformation;
import com.data.dataxer.models.domain.Company;
import com.data.dataxer.repositories.BillingInformationRepository;

import java.util.List;

public class BillingInformationServiceImpl implements BillingInformationService {
    private final BillingInformationRepository billingInformationRepository;

    public BillingInformationServiceImpl(BillingInformationRepository billingInformationRepository) {
        this.billingInformationRepository = billingInformationRepository;
    }

    @Override
    public void store(Company company, List<BillingInformation> billingInformation) {
        billingInformation.forEach(b -> {
            b.setCompany(company);

            this.billingInformationRepository.save(b);
        });
    }

    @Override
    public void update(List<BillingInformation> billingInformation) {

    }
}
