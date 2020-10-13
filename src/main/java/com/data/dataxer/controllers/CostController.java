package com.data.dataxer.controllers;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.mappers.CostMapper;
import com.data.dataxer.models.dto.CostDTO;
import com.data.dataxer.models.enums.CostState;
import com.data.dataxer.services.CostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/costs")
public class CostController {

    private final CostService costService;
    private final CostMapper costMapper;

    public CostController(CostService costService, CostMapper costMapper) {
        this.costService = costService;
        this.costMapper = costMapper;
    }

    @PostMapping("/store")
    public ResponseEntity<CostDTO> store(@RequestBody CostDTO costDTO) {
        return ResponseEntity.ok(this.costMapper.costToCostDTO(
                this.costService.store(this.costMapper.costDTOToCost(costDTO))));
    }

    @PostMapping("/update")
    public ResponseEntity<CostDTO> update(@RequestBody CostDTO costDTO) {
        return ResponseEntity.ok(this.costMapper.costToCostDTO(
                this.costService.update(this.costMapper.costDTOToCost(costDTO))));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<CostDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sortColumn,
            @RequestParam(value = "order", defaultValue = "desc") String order,
            @RequestParam(value = "filters", defaultValue = "") String filters
    ) {
        List<Filter> listOfFilters = Filter.resolveFiltersFromString(filters);
        Pageable pageable;
        if (order.equals("desc")) {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortColumn)));
        } else {
            pageable = PageRequest.of(page, size, Sort.by(Sort.Order.asc(sortColumn)));
        }

        return ResponseEntity.ok(this.costService
                .paginate(pageable, listOfFilters).map(this.costMapper::costToCostDTO));
    }

    @GetMapping("/changeState")
    public ResponseEntity<CostDTO> changeState(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "state", defaultValue = "PAYED") CostState state) {
        return ResponseEntity.ok(this.costMapper.costToCostDTO(this.costService.changeState(id, state)));
    }

    @RequestMapping(value = "/duplicate/{id}")
    public ResponseEntity<CostDTO> duplicate(@PathVariable Long id) {
        return ResponseEntity.ok(this.costMapper.costToCostDTO(this.costService.duplicate(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.costService.destroy(id);
    }

}
