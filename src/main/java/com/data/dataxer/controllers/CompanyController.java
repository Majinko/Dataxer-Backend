package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CompanyMapper;
import com.data.dataxer.models.dto.CompanyDTO;
import com.data.dataxer.services.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'Company', 'Company')")
    public ResponseEntity<CompanyDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(companyMapper.toCompanyWithBillingInfoDTO(this.companyService.findById(id)));
    }

    @GetMapping("/all")
    @PreAuthorize("hasPermission(null, 'Company', 'Company')")
    public ResponseEntity<List<CompanyDTO>> all() {
        return ResponseEntity.ok(companyMapper.toCompaniesDTO(this.companyService.findAll()));
    }

    @PostMapping("/store")
    @PreAuthorize("hasPermission(null, 'Company', 'Company')")
    public ResponseEntity<CompanyDTO> store(@RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyMapper.toCompanyDTO(this.companyService.store(companyMapper.toCompanyWithBillingInfo(companyDTO))));
    }

    @PostMapping("/update")
    @PreAuthorize("hasPermission(null, 'Company', 'Company')")
    public ResponseEntity<CompanyDTO> update(@RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyMapper.toCompanyWithBillingInfoDTO(this.companyService.update(companyMapper.toCompany(companyDTO))));
    }

    @GetMapping("/default")
    public ResponseEntity<CompanyDTO> defaultCompany() {
        return ResponseEntity.ok(companyMapper.toCompanyDTO(this.companyService.getDefaultCompany()));
    }

    @GetMapping("/destroy/{id}")
    @PreAuthorize("hasPermission(null, 'Company', 'Company')")
    public void destroy(@PathVariable Long id) {
        this.companyService.destroy(id);
    }
}
