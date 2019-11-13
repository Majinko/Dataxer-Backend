package com.data.dataxer.controllers;

import com.data.dataxer.mappers.UserMapper;
import com.data.dataxer.models.dto.TokenDTO;
import com.data.dataxer.models.dto.UserDTO;
import com.data.dataxer.services.AuthService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthController(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/sign-up")
    public void signUp(@RequestBody UserDTO userDTO) {
        authService.signUp(userMapper.toUser(userDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(authService.auth(userMapper.toUser(userDTO)));
    }
}
