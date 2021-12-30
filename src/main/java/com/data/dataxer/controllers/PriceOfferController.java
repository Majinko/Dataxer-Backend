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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/priceOffer")
@PreAuthorize("hasPermission(null, 'Document', 'Document')")
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
            @RequestParam(value = "sortExpression", defaultValue = "sort(-priceOffer.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(priceOfferService.paginate(pageable, rqlFilter, sortExpression).map(priceOfferMapper::priceOfferToPriceOfferDTOSimple));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<PriceOfferDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(priceOfferMapper.priceOfferToPriceOfferDTO(this.priceOfferService.getById(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.priceOfferService.destroy(id);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<List<PriceOfferDTO>> findAllByProject(
            @PathVariable Long projectId,
            @RequestParam(value = "companyIds", required = false) List<Long> companyIds
    ) {
        return ResponseEntity.ok(priceOfferMapper.priceOffersToPriceOfferDTOsWithoutRelation(this.priceOfferService.findAllByProject(projectId, companyIds)));
    }

    @GetMapping("/duplicate/{oldPriceOfferId}")
    public ResponseEntity<PriceOfferDTO> duplicate(@PathVariable Long oldPriceOfferId) {
        return ResponseEntity.ok(priceOfferMapper.priceOfferToPriceOfferDTO(this.priceOfferService.duplicate(oldPriceOfferId)));
    }

}
