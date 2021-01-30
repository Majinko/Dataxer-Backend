package com.data.dataxer.controllers;

import com.data.dataxer.mappers.ContactMapper;
import com.data.dataxer.models.dto.ContactDTO;
import com.data.dataxer.services.ContactService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact")
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

    @PutMapping("/update")
    public ResponseEntity<ContactDTO> update(@RequestBody ContactDTO contactDto) {
        return ResponseEntity.ok(contactMapper.toContactDto(contactService.update(contactMapper.toContact(contactDto))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contactMapper.toContactDto(contactService.getById(id)));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<ContactDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+contact.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(contactService.paginate(pageable, rqlFilter, sortExpression).map(contactMapper::toContactDto));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        contactService.destroy(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ContactDTO>> all() {
        return ResponseEntity.ok(contactMapper.toContactDTOs(contactService.findAll()));
    }
}
