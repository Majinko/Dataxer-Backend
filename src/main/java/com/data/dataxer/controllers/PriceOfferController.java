package com.data.dataxer.controllers;

import com.data.dataxer.mappers.PriceOfferMapper;
import com.data.dataxer.models.dto.PriceOfferDTO;
import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.services.DocumentNumberGeneratorService;
import com.data.dataxer.services.PriceOfferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/price-offer")
public class PriceOfferController {
    private final PriceOfferService priceOfferService;
    private final PriceOfferMapper priceOfferMapper;
    private final DocumentNumberGeneratorService documentNumberGeneratorService;

    public PriceOfferController(PriceOfferService priceOfferService, PriceOfferMapper priceOfferMapper, DocumentNumberGeneratorService documentNumberGeneratorService) {
        this.priceOfferService = priceOfferService;
        this.priceOfferMapper = priceOfferMapper;
        this.documentNumberGeneratorService = documentNumberGeneratorService;
    }

    @PostMapping("/store")
    public void store(@RequestBody PriceOfferDTO priceOfferDTO) {
        this.documentNumberGeneratorService.generateNextNumberByDocumentType(DocumentType.valueOf("PRICE_OFFER"), true);

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
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+priceOffer.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(priceOfferService.paginate(pageable, rqlFilter, sortExpression, false).map(priceOfferMapper::priceOfferToPriceOfferDTOSimple));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PriceOfferDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(priceOfferMapper.priceOfferToPriceOfferDTO(this.priceOfferService.getById(id, false)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.priceOfferService.destroy(id);
    }
}
