package com.data.dataxer.services;

import com.data.dataxer.models.domain.AppUser;
import com.data.dataxer.models.domain.Company;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.AppUserRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final AppUserRepository appUserRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, AppUserRepository appUserRepository) {
        this.companyRepository = companyRepository;
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional
    public Company store(Company company) {
        AppUser appUser = appUserRepository
                .findById(SecurityUtils.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        company.setAppUser(appUser);
        this.companySetBi(company);

        Company c = this.companyRepository.save(company);

        appUser.getCompanies().add(c);
        appUserRepository.save(appUser);

        return c;
    }

    public void companySetBi(Company company) {
        company.getBillingInformation().forEach(billingInformation -> {
            billingInformation.setCompany(company);
        });
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.findByAppUserId(SecurityUtils.id());
    }

    @Override
    public Company findById(Long id) {
        return companyRepository.findAllByIdAndAppUserId(id, SecurityUtils.id()).orElse(null);
    }

    @Override
    @Transactional
    public Company update(Company company, Long id) {
        Company c = this.findById(id);

        c.setName(company.getName());
        c.setLegalForm(company.getLegalForm());
        c.setStreet(company.getStreet());
        c.setCity(company.getCity());
        c.setPostalCode(company.getPostalCode());
        c.setCountry(company.getCountry());
        c.setEmail(company.getEmail());
        c.setPhone(company.getPhone());
        c.setWeb(company.getWeb());
        c.setIdentifyingNumber(company.getIdentifyingNumber());
        c.setVat(company.getVat());
        c.setNetOfVat(company.getNetOfVat());
        c.setIban(company.getIban());

        // company set bi
        c.getBillingInformation().clear();
        c.getBillingInformation().addAll(company.getBillingInformation());
        companySetBi(c);

        companyRepository.save(c);

        return c;
    }

    @Override
    public Company getDefaultCompany() {
        return this.companyRepository.findByDefaultCompanyAndAppUserId(true, SecurityUtils.id())
                .orElseThrow(() -> new RuntimeException("Default company not exist, please set it"));
    }
}
