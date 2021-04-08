package com.data.dataxer.controllers;

import com.data.dataxer.mappers.PrivilegeMapper;
import com.data.dataxer.models.dto.PrivilegeDTO;
import com.data.dataxer.services.PrivilegeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/privilege")
@PreAuthorize("hasPermission(null, 'Settings', 'Settings')")
public class PrivilegeController {

    private final PrivilegeService privilegeService;
    private final PrivilegeMapper privilegeMapper;

    public PrivilegeController(PrivilegeService privilegeService, PrivilegeMapper privilegeMapper) {
        this.privilegeService = privilegeService;
        this.privilegeMapper = privilegeMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<PrivilegeDTO>> getAll() {
        return ResponseEntity.ok(this.privilegeMapper.privilegesToPrivilegeDTOS(this.privilegeService.getAll()));
    }
}
