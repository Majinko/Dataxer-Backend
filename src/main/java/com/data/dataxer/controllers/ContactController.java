package com.data.dataxer.controllers;

import com.data.dataxer.mappers.ContactMapper;
import com.data.dataxer.models.dto.ContactDTO;
import com.data.dataxer.services.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
@PreAuthorize("hasPermission(null, 'Contact', 'Contact')")
public class ContactController {
    private final ContactService contactService;
    private final ContactMapper contactMapper;

    public ContactController(ContactService contactService, ContactMapper contactMapper) {
        this.contactService = contactService;
        this.contactMapper = contactMapper;
    }

    @PostMapping("/store")
    public ResponseEntity<ContactDTO> store(@RequestBody ContactDTO contactDto) {
        return ResponseEntity.ok(contactMapper.toContactDto(contactService.store(contactMapper.toContact(contactDto))));
    }

    @PostMapping("/update")
    public ResponseEntity<ContactDTO> update(@RequestBody ContactDTO contactDto) {
        return ResponseEntity.ok(contactMapper.toContactDto(contactService.update(contactMapper.toContact(contactDto))));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        contactService.destroy(id);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<ContactDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contactMapper.toContactDto(contactService.getById(id)));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<ContactDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(-contact.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(contactService.paginate(pageable, rqlFilter, sortExpression).map(contactMapper::toContactDto));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ContactDTO>> all() {
        return ResponseEntity.ok(contactMapper.toContactDTOs(contactService.findAll()));
    }

    @GetMapping("/allHasCost")
    public ResponseEntity<List<ContactDTO>> allHasCost() {
        return ResponseEntity.ok(contactMapper.toContactDTOs(this.contactService.allHasCost()));
    }

    @GetMapping("/allHasInvoice")
    public ResponseEntity<List<ContactDTO>> allHasInvoice() {
        return ResponseEntity.ok(contactMapper.toContactDTOs(this.contactService.allHasInvoice()));
    }

    @GetMapping("/allHasPriceOffer")
    public ResponseEntity<List<ContactDTO>> allHasPriceOffer() {
        return ResponseEntity.ok(contactMapper.toContactDTOs(this.contactService.allHasPriceOffer()));
    }

    @GetMapping("/allHasProject")
    public ResponseEntity<List<ContactDTO>> allHasProject() {
        return ResponseEntity.ok(contactMapper.toContactDTOs(this.contactService.allHasProject()));
    }
}
