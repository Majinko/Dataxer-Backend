package com.data.dataxer.controllers;

import com.data.dataxer.mappers.SettingsMapper;
import com.data.dataxer.models.dto.SettingsDTO;
import com.data.dataxer.services.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@PreAuthorize("hasPermission(null, 'Settings', 'Settings')")
public class SettingsController {

    private final SettingsService settingsService;
    private final SettingsMapper settingsMapper;

    public SettingsController(SettingsService settingsService, SettingsMapper settingsMapper) {
        this.settingsService = settingsService;
        this.settingsMapper = settingsMapper;
    }

    @RequestMapping(value = "/make/{companyId}", method = RequestMethod.POST)
    public void makeCompanySettings(@PathVariable Long companyId) {
        this.settingsService.makeSettingsForCompany(companyId);
    }

    @GetMapping("/getAll/{appProfileId}")
    public ResponseEntity<List<SettingsDTO>> getAllCompanySettings(@PathVariable Long appProfileId) {
        return ResponseEntity.ok(this.settingsMapper.settingsListToSettingsDTOList(this.settingsService.getCompanySettings(appProfileId)));
    }

    @GetMapping("/getByName/{settingName}")
    public ResponseEntity<SettingsDTO> getSettingByName(@PathVariable String settingName) {
        return ResponseEntity.ok(this.settingsMapper.settingsToSettingsDTO(this.settingsService.getByName(settingName)));
    }
}
