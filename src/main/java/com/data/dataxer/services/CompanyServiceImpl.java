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

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public Company store(Company company) {
        company.setAppUsers(List.of(SecurityUtils.loggedUser()));

        if (this.getDefaultCompany() == null) {
            company.setDefaultCompany(true);
        }

        return this.companyRepository.save(company);
    }

    @Override
    public List<Company> findAll() {
        return this.companyRepository.findAllByAppUsersIn(List.of(SecurityUtils.loggedUser()));
    }

    @Override
    public Company findById(Long id) {
        return companyRepository.findByIdAndAppUsersIn(id, List.of(SecurityUtils.loggedUser())).orElseThrow(() -> {
            throw new RuntimeException("Company is not exist");
        });
    }

    @Override
    public Company update(Company oldCompany) {
        return companyRepository.findByIdAndAppUsersIn(oldCompany.getId(), List.of(SecurityUtils.loggedUser())).map(company -> {

            company.setName(oldCompany.getName());
            company.setLogoUrl(oldCompany.getLogoUrl());
            company.setLegalForm(oldCompany.getLegalForm());
            company.setStreet(oldCompany.getStreet());
            company.setCity(oldCompany.getCity());
            company.setPostalCode(oldCompany.getPostalCode());
            company.setCountry(oldCompany.getCountry());
            company.setEmail(oldCompany.getEmail());
            company.setPhone(oldCompany.getPhone());
            company.setWeb(oldCompany.getWeb());
            company.setIban(oldCompany.getIban());
            company.setCin(oldCompany.getTin());
            company.setTin(oldCompany.getTin());
            company.setVatin(oldCompany.getVatin());

            return companyRepository.save(company);
        }).orElse(null);
    }

    @Override
    public Company getDefaultCompany() {
        return SecurityUtils.defaultCompany();
    }

    @Override
    public void destroy(Long id) {
        Company c = this.findById(id);

        if (c.getDefaultCompany() != null) {
            throw new RuntimeException("Default company cannot destroy");
        }

        this.companyRepository.delete(c);
    }

    @Override
    public void switchCompany(Long id) {
        List<Company> companies = this.findAll();

        companies.forEach(company -> {
            company.setDefaultCompany(company.getId().equals(id));
        });

        companyRepository.saveAll(companies);
    }
}
