package com.data.dataxer.controllers;

import com.data.dataxer.mappers.MailAccountsMapper;
import com.data.dataxer.models.dto.MailAccountsDTO;
import com.data.dataxer.models.dto.MailDataDTO;
import com.data.dataxer.services.MailAccountsService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mailAccounts")
@PreAuthorize("hasPermission(null, 'Settings', 'Settings')")
public class MailAccountsController {

    private final MailAccountsService mailAccountsService;
    private final MailAccountsMapper mailAccountsMapper;

    public MailAccountsController(MailAccountsService mailAccountsService, MailAccountsMapper mailAccountsMapper) {
        this.mailAccountsService = mailAccountsService;
        this.mailAccountsMapper = mailAccountsMapper;
    }

    @PostMapping("/store")
    public void store(@RequestBody MailAccountsDTO mailAccountsDTO) {
        this.mailAccountsService.store(this.mailAccountsMapper.mailAccountsDTOToMailAccounts(mailAccountsDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody MailAccountsDTO mailAccountsDTO) {
        this.mailAccountsService.update(this.mailAccountsMapper.mailAccountsDTOToMailAccounts(mailAccountsDTO));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<MailAccountsDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(this.mailAccountsMapper.mailAccountsToMailAccountsDTO(
                this.mailAccountsService.getById(id)
        ));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<MailAccountsDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+mailAccounts.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(this.mailAccountsService.paginate(pageable, rqlFilter, sortExpression)
            .map(this.mailAccountsMapper::mailAccountsToMailAccountsDTO));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.mailAccountsService.destroy(id);
    }

    @GetMapping("/activate/{id}")
    public void activate(@PathVariable Long id) {
        this.mailAccountsService.activate(id);
    }

    @GetMapping("/deactivate/{id}")
    public void deactivate(@PathVariable Long id) {
        this.mailAccountsService.deactivate(id);
    }

    @PostMapping("/sendEmail")
    public void sendEmail(@RequestBody MailDataDTO mailDataDTO) {
        this.mailAccountsService.sendEmail(mailDataDTO.getSubject(), mailDataDTO.getContent(), mailDataDTO.getParticipantIds(), mailDataDTO.getCompanyId(), mailDataDTO.getTemplateId());
    }

    @PostMapping("/sendSimpleEmail")
    public void sendEmailWithAttachments(@RequestBody MailDataDTO mailDataDTO) {
        this.mailAccountsService.sendEmailWithAttachments(mailDataDTO.getSubject(), mailDataDTO.getContent(),
                mailDataDTO.getParticipantIds(), mailDataDTO.getTemplateId(), mailDataDTO.getFileNames());
    }
}
