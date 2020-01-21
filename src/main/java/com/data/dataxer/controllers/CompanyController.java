package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CompanyMapper;
import com.data.dataxer.models.dto.CompanyDTO;
import com.data.dataxer.services.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/company")
public class CompanyController {
    private final CompanyService companyService;
    private final CompanyMapper companyMapper;

    public CompanyController(CompanyService companyService, CompanyMapper companyMapper) {
        this.companyService = companyService;
        this.companyMapper = companyMapper;
    }

    @PostMapping("/store")
    public ResponseEntity<CompanyDTO> store(@RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyMapper.toCompanyDTO(this.companyService.store(companyMapper.toCompany(companyDTO))));
    }
}
