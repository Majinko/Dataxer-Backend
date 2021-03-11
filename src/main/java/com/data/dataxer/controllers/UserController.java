package com.data.dataxer.controllers;

import com.data.dataxer.mappers.SalaryMapper;
import com.data.dataxer.mappers.UserMapper;
import com.data.dataxer.models.dto.AppUserDTO;
import com.data.dataxer.models.dto.AppUserInitDTO;
import com.data.dataxer.models.dto.AppUserOverviewDTO;
import com.data.dataxer.services.SalaryService;
import com.data.dataxer.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final SalaryService salaryService;
    private final SalaryMapper salaryMapper;

    public UserController(UserService userService, UserMapper userMapper, SalaryService salaryService, SalaryMapper salaryMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.salaryService = salaryService;
        this.salaryMapper = salaryMapper;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AppUserDTO>> all() {
        return ResponseEntity.ok(userMapper.appUserToAppUserDTOs(this.userService.all()));
    }

    @GetMapping("/{uid}")
    public ResponseEntity<AppUserDTO> getByUid(@PathVariable String uid) {
        return ResponseEntity.ok(userMapper.appUserToAppUserDTO(this.userService.getByUid(uid)));
    }

    @GetMapping("/overview")
    public ResponseEntity<List<AppUserOverviewDTO>> overview() {
        return ResponseEntity.ok(this.userService.overview());
    }

    @GetMapping("/logged")
    public ResponseEntity<AppUserDTO> getLoggedUser() {
        return ResponseEntity.ok(userMapper.appUserToAppUserDTO(this.userService.loggedUser()));
    }

    @GetMapping("/destroy/{uid}")
    public void destroy(@PathVariable String uid) {
        this.userService.destroy(uid);
    }

    @PostMapping("/store")
    public void store(@RequestBody AppUserInitDTO appUserInitDTO) {
        this.salaryService.initUserStoreSalary(
                salaryMapper.salaryDTOtoSalary(appUserInitDTO.getSalary()),
                userMapper.appUserDTOtoAppUser(userMapper.appUserToAppUserDTO(userService.store(userMapper.appUserDTOtoAppUser(appUserInitDTO.getUser()))))
        );
    }

    @PostMapping("/update")
    public void update(@RequestBody AppUserDTO appUserDTO) {
        ResponseEntity.ok(userMapper.appUserToAppUserDTO(userService.update(userMapper.appUserDTOtoAppUser(appUserDTO))));
    }
}
