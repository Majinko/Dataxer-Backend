package com.data.dataxer.controllers;

import com.data.dataxer.services.OverviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private final OverviewService overviewService;

    public TestController(OverviewService overviewService) {
        this.overviewService = overviewService;
    }

    @GetMapping("/methodTest")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok(this.overviewService.executeUsersYearsHours(null));
    }

}
