package com.data.dataxer.controllers;

import com.data.dataxer.mappers.SettingsMapper;
import com.data.dataxer.models.dto.SettingsDTO;
import com.data.dataxer.services.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @RequestMapping(value =  "/make/{companyId}", method = RequestMethod.POST)
    public void makeCompanySettings(@PathVariable Long companyId) {
        this.settingsService.makeSettingsForCompany(companyId);
    }

    @GetMapping("/getAll/{companyId}")
    public ResponseEntity<List<SettingsDTO>> getAllCompanySettings(@PathVariable Long companyId) {
        return ResponseEntity.ok(this.settingsMapper.settingsListToSettingsDTOList(this.settingsService.getCompanySettings(companyId)));
    }

    @GetMapping("/getByName/{settingName}")
    public ResponseEntity<SettingsDTO> getSettingByName(@PathVariable String settingName) {
        return ResponseEntity.ok(this.settingsMapper
                .settingsToSettingsDTO(this.settingsService.getByName(settingName)));
    }

    @PostMapping("/destroyCompanySettings/{companyId}")
    public void destroyAllCompanySettings(@PathVariable Long companyId) {
        this.settingsService.destroyAllCompanySettings(companyId);
    }

    @PutMapping("/regenerateCompanySettings/{companyId}")
    public void regenerateCompanySettings(@PathVariable Long companyId) {
        this.settingsService.regenerateCompanySettings(companyId);
    }
}
