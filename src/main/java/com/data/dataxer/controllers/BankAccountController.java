package com.data.dataxer.controllers;

import com.data.dataxer.mappers.BankAccountMapper;
import com.data.dataxer.models.dto.BankAccountDTO;
import com.data.dataxer.services.BankAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bank-account")
public class BankAccountController {
    private final BankAccountService bankAccountService;
    private final BankAccountMapper bankAccountMapper;

    public BankAccountController(BankAccountService bankAccountService, BankAccountMapper bankAccountMapper) {
        this.bankAccountService = bankAccountService;
        this.bankAccountMapper = bankAccountMapper;
    }

    @GetMapping("")
    public ResponseEntity<List<BankAccountDTO>> findAll() {
        return ResponseEntity.ok(bankAccountMapper.bankAccountsToBankAccountDTOs(this.bankAccountService.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccountDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(bankAccountMapper.bankAccountToBankAccountDTO(this.bankAccountService.getById(id)));
    }

    @PostMapping("/store")
    public ResponseEntity<BankAccountDTO> store(@RequestBody BankAccountDTO bankAccountDTO) {
        return ResponseEntity.ok(bankAccountMapper.bankAccountToBankAccountDTO(bankAccountService.store(bankAccountMapper.bankAccountDTOtoBankAccount(bankAccountDTO))));
    }

    @PostMapping("/update")
    public ResponseEntity<BankAccountDTO> update(@RequestBody BankAccountDTO bankAccountDTO) {
        return ResponseEntity.ok(bankAccountMapper.bankAccountToBankAccountDTO(bankAccountService.update(bankAccountMapper.bankAccountDTOtoBankAccount(bankAccountDTO))));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.bankAccountService.destroy(id);
    }

    @GetMapping("/set-default/{id}")
    public void setDefault(@PathVariable Long id) {
        this.bankAccountService.setDefaultBankAccount(id);
    }

    @GetMapping("/get-default")
    public ResponseEntity<BankAccountDTO> getDefault() {
        return ResponseEntity.ok(bankAccountMapper.bankAccountToBankAccountDTO(bankAccountService.getDefaultBankAccount()));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<BankAccountDTO>> getAll() {
        return ResponseEntity.ok(bankAccountMapper.bankAccountsToBankAccountDTOs(bankAccountService.findAll()));
    }
}
