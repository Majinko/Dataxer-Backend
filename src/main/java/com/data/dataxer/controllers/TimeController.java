package com.data.dataxer.controllers;

import com.data.dataxer.filters.Filter;
import com.data.dataxer.mappers.TimeMapper;
import com.data.dataxer.models.dto.TimeDTO;
import com.data.dataxer.services.TimeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/time")
public class TimeController {

    private final TimeService timeService;
    private final TimeMapper timeMapper;

    public TimeController(TimeService timeService, TimeMapper timeMapper) {
        this.timeService = timeService;
        this.timeMapper = timeMapper;
    }

    @PostMapping("/store")
    public ResponseEntity<TimeDTO> store(@RequestBody TimeDTO timeDTO) {
        return ResponseEntity.ok(this.timeMapper.timeToTimeDTO(this.timeService.store(this.timeMapper.timeDTOToTime(timeDTO))));
    }

    @PostMapping("/update")
    public ResponseEntity<TimeDTO> update(@RequestBody TimeDTO timeDTO) {
        return ResponseEntity.ok(this.timeMapper.timeToTimeDTO(this.timeService.update(this.timeMapper.timeDTOToTime(timeDTO))));
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<TimeDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "sort", defaultValue = "id") String sortColumn,
            @RequestBody(required = false) Filter filter
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortColumn)));

        return ResponseEntity.ok(this.timeService.paginate(pageable, filter).map(this.timeMapper::timeToTimeDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeDTO> getTimeById(@PathVariable Long id) {
        return ResponseEntity.ok(this.timeMapper.timeToTimeDTO(this.timeService.getTimeById(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(
            @PathVariable Long id
    ) {
        this.timeService.destroy(id);
    }
}
