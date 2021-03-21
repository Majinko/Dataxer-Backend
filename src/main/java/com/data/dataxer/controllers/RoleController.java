package com.data.dataxer.controllers;

import com.data.dataxer.mappers.RoleMapper;
import com.data.dataxer.models.dto.RoleDTO;
import com.data.dataxer.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Autowired
    RoleService roleService;

    @Autowired
    RoleMapper roleMapper;

    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> all() {
        return ResponseEntity.ok(roleMapper.rolesToRoleDTOs(this.roleService.getAll()));
    }

    @PostMapping("/storeOrUpdate")
    public void storeOrUpdate(@RequestBody RoleDTO roleDTO) {
        this.roleService.storeOrUpdate(this.roleMapper.roleDTOtoRole(roleDTO));
    }
}
