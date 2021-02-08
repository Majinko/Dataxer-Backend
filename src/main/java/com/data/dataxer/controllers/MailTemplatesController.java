package com.data.dataxer.controllers;

import com.data.dataxer.mappers.MailTemplatesMapper;
import com.data.dataxer.models.dto.MailTemplatesDTO;
import com.data.dataxer.services.MailTemplatesService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mailTemplates")
public class MailTemplatesController {

    private final MailTemplatesService mailTemplatesService;
    private final MailTemplatesMapper mailTemplatesMapper;

    public MailTemplatesController(MailTemplatesService mailTemplatesService, MailTemplatesMapper mailTemplatesMapper) {
        this.mailTemplatesService = mailTemplatesService;
        this.mailTemplatesMapper = mailTemplatesMapper;
    }

    @PostMapping("/store")
    public void store(@RequestBody MailTemplatesDTO mailTemplatesDTO) {
        this.mailTemplatesService.store(this.mailTemplatesMapper.mailTemplatesDTOToMailTemplates(mailTemplatesDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody MailTemplatesDTO mailTemplatesDTO) {
        this.mailTemplatesService.update(this.mailTemplatesMapper.mailTemplatesDTOToMailTemplates(mailTemplatesDTO));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<MailTemplatesDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                this.mailTemplatesMapper.mailTemplatesToMailTemplatesDTO(this.mailTemplatesService.getById(id))
        );
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<MailTemplatesDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+mailTemplates.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(
                this.mailTemplatesService.paginate(pageable, rqlFilter, sortExpression)
                    .map(mailTemplatesMapper::mailTemplatesToMailTemplatesDTO)
        );
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.mailTemplatesService.destroy(id);
    }

}
