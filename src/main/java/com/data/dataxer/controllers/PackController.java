package com.data.dataxer.controllers;

import com.data.dataxer.mappers.PackMapper;
import com.data.dataxer.models.dto.PackDTO;
import com.data.dataxer.services.PackService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pack")
public class PackController {
    private final PackMapper packMapper;
    private final PackService packService;

    public PackController(PackMapper packMapper, PackService packService) {
        this.packMapper = packMapper;
        this.packService = packService;
    }

    @PostMapping("/store")
    public void store(@RequestBody PackDTO packDTO) {
        this.packService.store(packMapper.packDTOtoPack(packDTO));
    }


    @PostMapping("/update")
    public void update(@RequestBody PackDTO packDTO) {
        this.packService.update(packMapper.packDTOtoPack(packDTO));
    }

    @GetMapping("/paginate")
    public ResponseEntity<Page<PackDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(this.packService.paginate(pageable).map(packMapper::packToPackDTOSimple));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PackDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(packMapper.packToPackDTO(this.packService.getById(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.packService.destroy(id);
    }
}
