package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CostMapper;
import com.data.dataxer.mappers.StorageMapper;
import com.data.dataxer.models.dto.CostDTO;
import com.data.dataxer.models.dto.UploadContextDTO;
import com.data.dataxer.models.enums.CostState;
import com.data.dataxer.services.CostService;
import com.data.dataxer.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cost")
public class CostController {
    @Autowired
    private CostService costService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private CostMapper costMapper;

    @Autowired
    private StorageMapper storageMapper;


    @PostMapping("/store")
    public ResponseEntity<CostDTO> store(@RequestBody UploadContextDTO<CostDTO> uploadContext) {
        CostDTO cost = this.costMapper.costToCostDTO(this.costService.store(this.costMapper.costDTOToCost(uploadContext.getObject())));

        if (!uploadContext.getFiles().isEmpty()) {
            uploadContext.getFiles().forEach(file -> {
                this.storageService.store(storageMapper.storageFileDTOtoStorage(file), cost.getId(), "cost");
            });
        }

        return ResponseEntity.ok(cost);
    }


    @GetMapping("/{id}")
    public ResponseEntity<CostDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.costMapper.costToCostDTO(this.costService.getByIdWithRelation(id)));
    }


    @PostMapping("/update")
    public ResponseEntity<CostDTO> update(@RequestBody UploadContextDTO<CostDTO> uploadContext) {
        CostDTO cost = this.costMapper.costToCostDTO(this.costService.update(this.costMapper.costDTOToCost(uploadContext.getObject())));

        if (!uploadContext.getFiles().isEmpty()) {
            uploadContext.getFiles().forEach(file -> {
                this.storageService.store(storageMapper.storageFileDTOtoStorage(file), cost.getId(), "cost");
            });
        }

        return ResponseEntity.ok(cost);
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<CostDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+cost.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(this.costService.paginate(pageable, rqlFilter, sortExpression).map(this.costMapper::costToCostDTOPaginate));
    }

    @GetMapping("/changeState")
    public ResponseEntity<CostDTO> changeState(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "state", defaultValue = "PAYED") CostState state) {
        return ResponseEntity.ok(this.costMapper.costToCostDTO(this.costService.changeState(id, state)));
    }

    @GetMapping(value = "/duplicate/{id}")
    public ResponseEntity<CostDTO> duplicate(@PathVariable Long id) {
        return ResponseEntity.ok(this.costMapper.costToCostDTO(this.costService.duplicate(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.costService.destroy(id);
    }
}
