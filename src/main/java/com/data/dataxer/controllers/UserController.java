package com.data.dataxer.controllers;

import com.data.dataxer.mappers.UserMapper;
import com.data.dataxer.models.dto.AppUserDTO;
import com.data.dataxer.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/logged")
    public ResponseEntity<AppUserDTO> getLoggedUser() {
        return ResponseEntity.ok(userMapper.appUserToAppUserDTO(this.userService.loggedUser()));
    }

    @PostMapping("/store")
    public void store(@RequestBody AppUserDTO appUserDTO) {
        ResponseEntity.ok(userMapper.appUserToAppUserDTO(userService.store(userMapper.appUserDTOtoAppUser(appUserDTO))));
    }
}
