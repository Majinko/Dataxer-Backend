package com.data.dataxer.controllers;

import com.data.dataxer.mappers.UserMapper;
import com.data.dataxer.models.dto.ContactDTO;
import com.data.dataxer.models.dto.DataxerUserDTO;
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
    public ResponseEntity<DataxerUserDTO> getLoggedUser() {
        return ResponseEntity.ok(userMapper.toDataxerUserDTO(this.userService.loggedUser()));
    }

    @PostMapping("/store")
    public void store(@RequestBody DataxerUserDTO dataxerUserDTO) {
        ResponseEntity.ok(userMapper.toDataxerUserDTO(userService.store(userMapper.toDataxerUser(dataxerUserDTO))));
    }
}
