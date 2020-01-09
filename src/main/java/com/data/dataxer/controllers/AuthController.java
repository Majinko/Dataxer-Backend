package com.data.dataxer.controllers;

import com.data.dataxer.mappers.UserMapper;
import com.data.dataxer.models.dto.DataxerUserDTO;
import com.data.dataxer.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserService userService;
    private final UserMapper userMapper;

    public AuthController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping("/store")
    public ResponseEntity<DataxerUserDTO> store(@RequestBody DataxerUserDTO dataxerUserDTO) {
        return ResponseEntity.ok(userMapper.toDataxerUserDTO(userService.store(userMapper.toDataxerUser(dataxerUserDTO))));
    }
}
