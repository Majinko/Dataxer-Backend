package com.data.dataxer.controllers;

import com.data.dataxer.mappers.AppProfileMapper;
import com.data.dataxer.models.dto.AppProfileDTO;
import com.data.dataxer.services.AppProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/all")
    public ResponseEntity<List<AppProfileDTO>> getAll() {
        return ResponseEntity.ok(this.appProfileMapper.appProfilesToAppProfileDTOs(this.appProfileService.getAll()));
    }

    @PostMapping("/store")
    public void store(@RequestBody AppProfileDTO appProfileDTO) {
        this.appProfileService.store(appProfileMapper.appProfileDTOtoAppProfile(appProfileDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody AppProfileDTO appProfileDTO) {
        this.appProfileService.update(appProfileMapper.appProfileDTOtoAppProfile(appProfileDTO));
    }
}
