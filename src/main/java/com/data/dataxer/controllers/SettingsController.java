package com.data.dataxer.controllers;

import com.data.dataxer.mappers.SettingsMapper;
import com.data.dataxer.services.SettingsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
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


}
