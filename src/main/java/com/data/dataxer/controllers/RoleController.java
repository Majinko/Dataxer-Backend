package com.data.dataxer.controllers;

import com.data.dataxer.mappers.RoleMapper;
import com.data.dataxer.models.dto.RoleDTO;
import com.data.dataxer.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/role")
@PreAuthorize("hasPermission(null, 'Settings', 'Settings')")
public class RoleController {
    @Autowired
    RoleService roleService;

    @Autowired
    RoleMapper roleMapper;

    @PostMapping("/storeOrUpdate")
    public void storeOrUpdate(@RequestBody RoleDTO roleDTO) {
        this.roleService.storeOrUpdate(this.roleMapper.roleDTOtoRole(roleDTO));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<RoleDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.roleMapper.roleToRoleDTO(this.roleService.getById(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoleDTO>> all() {
        return ResponseEntity.ok(roleMapper.rolesToRoleDTOsSimple(this.roleService.getAll()));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<RoleDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(-role.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(roleService.paginate(pageable, rqlFilter, sortExpression).map(roleMapper::roleToRoleDTO));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.roleService.destroy(id);
    }
}
