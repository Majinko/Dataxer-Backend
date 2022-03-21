package com.data.dataxer.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demandItem")
@PreAuthorize("hasPermission(null, 'Demand', 'Demand')")
public class DemandItemController {

}
