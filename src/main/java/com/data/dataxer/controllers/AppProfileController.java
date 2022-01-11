package com.data.dataxer.controllers;

import com.data.dataxer.mappers.AppProfileMapper;
import com.data.dataxer.models.dto.AppProfileDTO;
import com.data.dataxer.services.AppProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/appProfile")
public class AppProfileController {
    @Autowired
    AppProfileService appProfileService;

    @Autowired
    AppProfileMapper appProfileMapper;

    @GetMapping("/defaultProfile")
    public ResponseEntity<AppProfileDTO> getDefaultUserAppProfile() {
        return ResponseEntity.ok(this.appProfileMapper.appProfileToAppProfileDTO(this.appProfileService.getDefaultUserAppProfile()));
    }
}
