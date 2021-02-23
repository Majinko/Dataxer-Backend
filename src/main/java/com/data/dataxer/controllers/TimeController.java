package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CategoryMapper;
import com.data.dataxer.mappers.ProjectMapper;
import com.data.dataxer.mappers.TimeMapper;
import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.models.dto.MonthAndYearDTO;
import com.data.dataxer.models.dto.ProjectDTO;
import com.data.dataxer.models.dto.TimeDTO;
import com.data.dataxer.services.TimeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/time")
public class TimeController {

    private final TimeService timeService;
    private final TimeMapper timeMapper;
    private final ProjectMapper projectMapper;
    private final CategoryMapper categoryMapper;

    public TimeController(TimeService timeService, TimeMapper timeMapper, ProjectMapper projectMapper,
                          CategoryMapper categoryMapper) {
        this.timeService = timeService;
        this.timeMapper = timeMapper;
        this.projectMapper = projectMapper;
        this.categoryMapper = categoryMapper;
    }

    @PostMapping("/store")
    public ResponseEntity<TimeDTO> store(@RequestBody @Valid TimeDTO timeDTO) {
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
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+time.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(this.timeService.paginate(pageable, rqlFilter, sortExpression).map(this.timeMapper::timeToTimeDTO));
    }

    @GetMapping("/allForPeriod")
    public ResponseEntity<List<TimeDTO>> allForPeriod(@RequestParam(value = "from")
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                                      @RequestParam(value = "to")
                                                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        return ResponseEntity.ok(this.timeMapper.timeListToTimeDTOList(this.timeService.allForPeriod(from, to)));
    }

    @GetMapping("/userMonths")
    public ResponseEntity<List<MonthAndYearDTO>> getAllUserMonths(@RequestParam(value = "id") Long userId) {
        return ResponseEntity.ok(this.timeService.getAllUserMonths(userId));
    }

    @GetMapping("/allUserProjects")
    public ResponseEntity<List<ProjectDTO>> getAllUserProjects(@RequestParam(value = "id") Long userId) {
        return ResponseEntity.ok(this.projectMapper.projectToProjectDTOs(this.timeService.getAllUserProjects(userId)));
    }

    @GetMapping("/lastUserProjects")
    public ResponseEntity<List<ProjectDTO>> lastUserProjects(@RequestParam(value = "id") Long id) {
        return ResponseEntity.ok(this.projectMapper.projectToProjectDTOs(this.timeService.getLastUserWorkingProjects(id)));
    }

    @GetMapping("/projectCategoryByTime")
    public ResponseEntity<List<CategoryDTO>> getProjectCategoryOrderByWorkDay(@RequestParam(value = "projectId") Long projectId) {
        return ResponseEntity.ok(this.categoryMapper.toCategoryDTOs(this.timeService.getProjectCategoryByTime(projectId)));
    }

    @GetMapping("/allProjectCategory")
    public ResponseEntity<List<CategoryDTO>> getProjectCategoryOrderByPosition(@RequestParam(value = "projectId") Long projectId) {
        return ResponseEntity.ok(this.categoryMapper.toCategoryDTOs(this.timeService.getProjectCategoryByPosition(projectId)));
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
