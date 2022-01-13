package com.data.dataxer.services;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.securityContextUtils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyServiceImpl implements CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Override
    @Transactional
    public Company store(Company company) {
        this.checkCanCreateCompany(company);

        company.setAppProfile(SecurityUtils.defaultProfile());
        company.setAppUsers(List.of(SecurityUtils.loggedUser()));

        return this.companyRepository.save(company);
    }

    @Override
    public List<Company> findAll() {
        return this.companyRepository.findAllByAppUsersInAndAppProfileIdOrderByPositionAsc(List.of(SecurityUtils.loggedUser()), SecurityUtils.defaultProfileId());
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
            company.setSignatureUrl(oldCompany.getSignatureUrl());
            company.setCompanyTaxType(oldCompany.getCompanyTaxType());
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
            company.setCin(oldCompany.getCin());
            company.setTin(oldCompany.getTin());
            company.setVatin(oldCompany.getVatin());

            return companyRepository.save(company);
        }).orElse(null);
    }

    @Override
    public void destroy(Long id) {
        Company c = this.findById(id);

        throw new RuntimeException("cannot destroy company");

        //this.companyRepository.delete(c);
    }

    @Override
    public void updatePosition(List<Company> companies) {
        companies.forEach(company -> {
            this.companyRepository.findByAppProfileIdAndId(company.getId(), SecurityUtils.defaultProfileId()).map(c -> {
                c.setPosition(company.getPosition());

                return companyRepository.save(c);
            });
        });
    }

    private void checkCanCreateCompany(Company company) {
        Optional<Company> c = this.companyRepository.findByCin(company.getCin());

        if (c.isPresent()) {
            throw new RuntimeException("company with cin " + company.getCin() + " exist");
        }
    }
}
