package com.data.dataxer.controllers;

import com.data.dataxer.mappers.UserMapper;
import com.data.dataxer.models.dto.UserDTO;
import com.data.dataxer.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<UserDTO> getLoggedUser() {
        return ResponseEntity.ok(userMapper.toUserDto(this.userService.loggedUser()));
    }
}
