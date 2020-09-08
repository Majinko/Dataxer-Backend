package com.data.dataxer.controllers;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.mappers.PriceOfferMapper;
import com.data.dataxer.models.dto.PriceOfferDTO;
import com.data.dataxer.models.filter.DocumentFilter;
import com.data.dataxer.services.PriceOfferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/price-offer")
public class PriceOfferController {
    private final PriceOfferService priceOfferService;
    private final PriceOfferMapper priceOfferMapper;

    public PriceOfferController(PriceOfferService priceOfferService, PriceOfferMapper priceOfferMapper) {
        this.priceOfferService = priceOfferService;
        this.priceOfferMapper = priceOfferMapper;
    }

    @PostMapping("/store")
    public void store(@RequestBody PriceOfferDTO priceOfferDTO) {
        this.priceOfferService.store(priceOfferMapper.priceOfferDTOtoPriceOffer(priceOfferDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody PriceOfferDTO priceOfferDTO) {
        this.priceOfferService.update(priceOfferMapper.priceOfferDTOtoPriceOffer(priceOfferDTO));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<PriceOfferDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sortColumn,
            @RequestBody(required = false) Filter params
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortColumn)));

        return ResponseEntity.ok(priceOfferService.paginate(pageable, params).map(priceOfferMapper::priceOfferToPriceOfferDTOSimple));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceOfferDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(priceOfferMapper.priceOfferToPriceOfferDTO(this.priceOfferService.getById(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.priceOfferService.destroy(id);
    }
}
