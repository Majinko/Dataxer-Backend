package com.data.dataxer.controllers;

import com.data.dataxer.mappers.ContactMapper;
import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.domain.Project;
import com.data.dataxer.models.dto.ContactDTO;
import com.data.dataxer.services.ContactService;
import com.querydsl.core.types.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

    @GetMapping("/filter")
    public List<Contact> filter(@QuerydslPredicate(root = Contact.class) Predicate predicate) {
        return contactService.filtering(predicate);
    }

    @GetMapping("/{id}")
    @Transactional
    public ResponseEntity<ContactDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(contactMapper.toContactDto(contactService.getById(id)));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ContactDTO>> all() {
        return ResponseEntity.ok(contactMapper.toContactDTOs(contactService.findAll()));
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public ResponseEntity<List<ContactDTO>> findByFirstNameAndLastName(@RequestParam(value = "firstName", defaultValue = "") String firstName, @RequestParam(value = "lastName", defaultValue = "") String lastName) {
        return ResponseEntity.ok(contactMapper.toContactDTOs(contactService.findByFirstNameAndLastName(firstName, lastName)));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<ContactDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "sort", defaultValue = "firstName") String sort,
            @RequestParam(value = "email", defaultValue = "") String email
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        return ResponseEntity.ok(contactService.paginate(pageable, email).map(contactMapper::toContactDto));
    }

    @PostMapping("/store")
    public ResponseEntity<ContactDTO> store(@RequestBody ContactDTO contactDto) {
        return ResponseEntity.ok(contactMapper.toContactDto(contactService.store(contactMapper.toContact(contactDto))));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ContactDTO> update(@RequestBody ContactDTO contactDto, @PathVariable Long id) {
        return ResponseEntity.ok(contactMapper.toContactDto(contactService.update(contactMapper.toContact(contactDto), id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        contactService.delete(id);
    }
}
