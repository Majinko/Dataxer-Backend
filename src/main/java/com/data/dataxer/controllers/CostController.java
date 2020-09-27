package com.data.dataxer.controllers;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.mappers.CostMapper;
import com.data.dataxer.models.dto.CostDTO;
import com.data.dataxer.services.CostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/cost")
public class CostController {

    private final CostService costService;
    private final CostMapper costMapper;

    public CostController(CostService costService, CostMapper costMapper) {
        this.costService = costService;
        this.costMapper = costMapper;
    }

    @PostMapping("/store")
    public void store(CostDTO costDTO) {
        this.costService.store(this.costMapper.costDTOToCost(costDTO));
    }

    @PostMapping("/update")
    public ResponseEntity<CostDTO> update(CostDTO costDTO) {
        return ResponseEntity.ok(this.costMapper.costToCostDTO(
                this.costService.update(this.costMapper.costDTOToCost(costDTO))));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<CostDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sortColumn,
            @RequestParam(value = "filters", defaultValue = "") String filters
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortColumn)));
        List<Filter> costFilters = Filter.resolveFiltersFromString(filters);
        return ResponseEntity.ok(this.costService
                .paginate(pageable, costFilters).map(this.costMapper::costToCostDTO));
    }

}
