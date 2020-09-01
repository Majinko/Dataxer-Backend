package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CompanyMapper;
import com.data.dataxer.models.domain.Company;
import com.data.dataxer.models.dto.CompanyDTO;
import com.data.dataxer.services.CompanyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/company")
public class CompanyController {
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    public CompanyController(CompanyService companyService, CompanyMapper companyMapper) {
        this.companyService = companyService;
        this.companyMapper = companyMapper;
    }

    @Transactional
    @GetMapping("/{id}")
    public ResponseEntity<CompanyDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(companyMapper.toCompanyWithBillingInfoDTO(this.companyService.findById(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<CompanyDTO>> all() {
        return ResponseEntity.ok(companyMapper.toCompaniesDTO(this.companyService.findAll()));
    }

    @PostMapping("/store")
    public ResponseEntity<CompanyDTO> store(@RequestBody CompanyDTO companyDTO) {
        Company companyResponse = this.companyService.store(companyMapper.toCompanyWithBillingInfo(companyDTO));
        try {
            this.companyService.createSettingsForCompany(companyResponse);
        } catch (IOException e) {
            return new ResponseEntity<>(new CompanyDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(companyMapper.toCompanyDTO(companyResponse));
    }

    @Transactional
    @PostMapping("/update/{id}")
    public ResponseEntity<CompanyDTO> update(@RequestBody CompanyDTO companyDTO, @PathVariable Long id) {
        return ResponseEntity.ok(companyMapper.toCompanyWithBillingInfoDTO(this.companyService.update(companyMapper.toCompany(companyDTO), id)));
    }
}
