package com.data.dataxer.controllers;

import com.data.dataxer.mappers.DemandMapper;
import com.data.dataxer.models.dto.DemandDTO;
import com.data.dataxer.services.DemandService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demand")
@PreAuthorize("hasPermission(null, 'Demand', 'Demand')")
public class DemandController {
    private final DemandMapper demandMapper;
    private final DemandService demandService;

    public DemandController(DemandMapper demandMapper, DemandService demandService) {
        this.demandMapper = demandMapper;
        this.demandService = demandService;
    }

    @PostMapping("/store")
    public void store(@RequestBody DemandDTO demandDTO) {
        this.demandService.store(demandMapper.demandDTOtoDemand(demandDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody DemandDTO demandDTO) {
        this.demandService.update(demandMapper.demandDTOtoDemand(demandDTO));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<DemandDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+demand.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        //return ResponseEntity.ok(demandService.paginate(pageable, rqlFilter, sortExpression).map(demandMapper::demandToDemandDTO));

        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<DemandDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(demandMapper.demandToDemandDTO(demandService.getById(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.demandService.destroy(id);
    }
}
