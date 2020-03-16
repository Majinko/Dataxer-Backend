package com.data.dataxer.services;
import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.domain.DataxerUser;
import com.data.dataxer.repositories.CompanyRepository;
import com.data.dataxer.repositories.DataxerUserRepository;
import com.data.dataxer.securityContextUtils.SecurityContextUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;
    private final DataxerUserRepository dataxerUserRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, DataxerUserRepository dataxerUserRepository) {
        this.companyRepository = companyRepository;
        this.dataxerUserRepository = dataxerUserRepository;
    }

    @Override
    @Transactional
    public Company store(Company company) {
        DataxerUser dataxerUser = dataxerUserRepository
                .findById(SecurityContextUtils.id())
                .orElseThrow(() -> new RuntimeException("User not found"));

        company.setDataxerUser(dataxerUser);
        this.companySetBi(company);

        Company c = this.companyRepository.save(company);

        dataxerUser.getCompanies().add(c);
        dataxerUserRepository.save(dataxerUser);

        return c;
    }

    public void companySetBi(Company company) {
        company.getBillingInformation().forEach(billingInformation -> {
            billingInformation.setCompany(company);
        });
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.findByDataxerUserId(SecurityContextUtils.id());
    }

    @Override
    public Company findById(Long id) {
        return companyRepository.findAllByIdAndDataxerUserId(id, SecurityContextUtils.id()).orElse(null);
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
}
