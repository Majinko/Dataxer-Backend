package com.data.dataxer.controllers;

import com.data.dataxer.mappers.SalaryMapper;
import com.data.dataxer.models.dto.SalaryDTO;
import com.data.dataxer.services.SalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salary")
@PreAuthorize("hasPermission(null, 'Settings', 'Settings')")
public class SalaryController {
    @Autowired
    private SalaryService salaryService;
    @Autowired
    private SalaryMapper salaryMapper;

    @GetMapping("/user/{uid}")
    public ResponseEntity<List<SalaryDTO>> getUserSalaries(
            @PathVariable String uid,
            @RequestParam(value = "sort", defaultValue = "asc") String sortExpression
    ) {
        Sort sort = Sort.by(sortExpression.equals("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, "start");

        return ResponseEntity.ok(this.salaryMapper.salariesToSalariesDto(this.salaryService.getUserSalaries(uid, sort)));
    }

    @PostMapping("/store")
    public void store(@RequestBody SalaryDTO salaryDTO) {
        this.salaryService.store(this.salaryMapper.salaryDTOtoSalary(salaryDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalaryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.salaryMapper.salaryToSalaryDTO(this.salaryService.getById(id)));
    }

    @PostMapping("/update")
    public void update(@RequestBody SalaryDTO salaryDTO) {
        this.salaryService.update(this.salaryMapper.salaryDTOtoSalary(salaryDTO));
    }
}
