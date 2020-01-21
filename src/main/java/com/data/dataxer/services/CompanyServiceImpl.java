package com.data.dataxer.services;

import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.DataxerUserRepository;
import com.data.dataxer.securityContextUtils.SecurityContextUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final DataxerUserRepository dataxerUserRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, DataxerUserRepository dataxerUserRepository) {
        this.companyRepository = companyRepository;
        this.dataxerUserRepository = dataxerUserRepository;
    }

    @Override
    public Company store(Company company) {
        DataxerUser dataxerUser = SecurityContextUtils.loggedUser();
        Company c = this.companyRepository.save(company);

        dataxerUser.setCompanies(new ArrayList<Company>() {{
            add(c);
        }});

        dataxerUserRepository.save(dataxerUser);

        return c;
    }
}
