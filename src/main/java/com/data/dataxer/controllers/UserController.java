package com.data.dataxer.controllers;

import com.data.dataxer.mappers.RoleMapper;
import com.data.dataxer.mappers.SalaryMapper;
import com.data.dataxer.mappers.UserMapper;
import com.data.dataxer.models.dto.AppUserDTO;
import com.data.dataxer.models.dto.AppUserInitDTO;
import com.data.dataxer.models.dto.AppUserOverviewDTO;
import com.data.dataxer.models.dto.RoleDTO;
import com.data.dataxer.services.SalaryService;
import com.data.dataxer.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
//@PreAuthorize("hasPermission(null, 'Settings', 'Settings')")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final SalaryService salaryService;
    private final SalaryMapper salaryMapper;
    private final RoleMapper roleMapper;

    public UserController(UserService userService, UserMapper userMapper, SalaryService salaryService, SalaryMapper salaryMapper, RoleMapper roleMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.salaryService = salaryService;
        this.salaryMapper = salaryMapper;
        this.roleMapper = roleMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AppUserDTO>> all() {
        return ResponseEntity.ok(userMapper.appUserToAppUserDTOs(this.userService.all()));
    }

    @GetMapping("/{uid}")
    public ResponseEntity<AppUserDTO> getByUid(@PathVariable String uid) {
        return ResponseEntity.ok(userMapper.appUserToAppUserDTO(this.userService.getByUid(uid)));
    }

    @GetMapping("/edit/{uid}")
    public ResponseEntity<AppUserDTO> edit(@PathVariable String uid) {
        return ResponseEntity.ok(userMapper.appUserToAppUserDTOWithRoles(this.userService.userWithRoles(uid)));
    }

    @GetMapping("/overview/{uid}")
    public ResponseEntity<AppUserOverviewDTO> userOverview(@PathVariable String uid) {
        return ResponseEntity.ok(this.userService.userOverview(uid));
    }

    @GetMapping("/overview")
    public ResponseEntity<Page<AppUserOverviewDTO>> overview(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "qString", required = false) String qString
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(this.userService.overview(pageable, qString));
    }

    // todo move to other controller
    @GetMapping("/logged")
    public ResponseEntity<AppUserDTO> getLoggedUser() {
        return ResponseEntity.ok(userMapper.appUserToAppUserDTOWithRoles(this.userService.loggedUser()));
    }

    @GetMapping("/destroy/{uid}")
    public void destroy(@PathVariable String uid) {
        this.userService.destroy(uid);
    }

    @PostMapping("/store")
    public void store(@RequestBody AppUserInitDTO appUserInitDTO) {
        this.salaryService.initUserStoreSalary(
                salaryMapper.salaryDTOtoSalary(appUserInitDTO.getSalary()),
                userMapper.appUserDTOtoAppUserWithRoles(userMapper.appUserToAppUserDTO(userService.store(userMapper.appUserDTOtoAppUserWithRoles(appUserInitDTO.getUser()))))
        );
    }

    @PostMapping("/update")
    public void update(@RequestBody AppUserDTO appUserDTO) {
        ResponseEntity.ok(userMapper.appUserToAppUserDTO(userService.update(userMapper.appUserDTOtoAppUserWithRoles(appUserDTO))));
    }

    @GetMapping("/switchProfile/{appProfileId}")
    public void switchProfile(@PathVariable Long appProfileId) {
        this.userService.switchProfile(appProfileId);
    }

    @PostMapping("/assignRoles/{uid}")
    public void assignRoles(@PathVariable String uid, @RequestBody List<RoleDTO> roleDTOS) {
        this.userService.assignRoles(uid, this.roleMapper.roleDTOStoRoles(roleDTOS));
    }
}
