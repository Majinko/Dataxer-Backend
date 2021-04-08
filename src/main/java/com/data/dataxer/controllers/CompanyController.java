package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CompanyMapper;
import com.data.dataxer.models.dto.CompanyDTO;
import com.data.dataxer.services.CompanyService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/company")
//@PreAuthorize("hasPermission(null, 'Settings', 'Settings')")
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
        return ResponseEntity.ok(companyMapper.toCompanyDTO(this.companyService.store(companyMapper.toCompanyWithBillingInfo(companyDTO))));
    }

    @PostMapping("/update")
    public ResponseEntity<CompanyDTO> update(@RequestBody CompanyDTO companyDTO) {
        return ResponseEntity.ok(companyMapper.toCompanyWithBillingInfoDTO(this.companyService.update(companyMapper.toCompany(companyDTO))));
    }

    @GetMapping("/default")
    public ResponseEntity<CompanyDTO> defaultCompany() {
        return ResponseEntity.ok(companyMapper.toCompanyDTO(this.companyService.getDefaultCompany()));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.companyService.destroy(id);
    }
}
