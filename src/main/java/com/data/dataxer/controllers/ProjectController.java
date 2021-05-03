package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CategoryMapper;
import com.data.dataxer.mappers.ProjectMapper;
import com.data.dataxer.mappers.TimeMapper;
import com.data.dataxer.models.dto.CategoryDTO;
import com.data.dataxer.models.dto.ProjectCategoryUserOverviewDTO;
import com.data.dataxer.models.dto.ProjectDTO;
import com.data.dataxer.models.dto.ProjectTimeOverviewDTO;
import com.data.dataxer.services.ProjectService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/project")
@PreAuthorize("hasPermission(null, 'Project', 'Project')")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final CategoryMapper categoryMapper;
    private final TimeMapper timeMapper;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper, CategoryMapper categoryMapper, TimeMapper timeMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.categoryMapper = categoryMapper;
        this.timeMapper = timeMapper;
    }

    @PostMapping("/store")
    public ResponseEntity<ProjectDTO> store(@RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectMapper.projectToProjectDTO(this.projectService.store(projectMapper.projectDTOtoProject(projectDTO))));
    }

    @GetMapping("/paginate")
    public ResponseEntity<Page<ProjectDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+project.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(projectService.paginate(pageable, rqlFilter, sortExpression).map(projectMapper::projectToProjectDTOWithoutCategory));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectMapper.projectToProjectDTO(this.projectService.getById(id)));
    }

    @GetMapping("/search/{queryString}")
    public ResponseEntity<List<ProjectDTO>> search(@PathVariable String queryString) {
        return ResponseEntity.ok(projectMapper.projectToProjectDTOs(this.projectService.search(queryString)));
    }

    @PostMapping("/update")
    public void update(@RequestBody ProjectDTO projectDTO) {
        this.projectService.update(projectMapper.projectDTOtoProject(projectDTO));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.projectService.destroy(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectDTO>> all() {
        return ResponseEntity.ok(projectMapper.projectToProjectDTOs(this.projectService.all()));
    }

    @GetMapping("/allProjectCategory")
    public ResponseEntity<List<CategoryDTO>> getAllProjectCategories(@RequestParam(value = "projectId") Long projectId) {
        return ResponseEntity.ok(this.categoryMapper.toCategoryDTOs(this.projectService.getAllProjectCategories(projectId)));
    }

    @GetMapping("/allProjectCategoriesByPosition")
    public ResponseEntity<List<CategoryDTO>> getAllProjectCategoriesOrderByDepthAndPosition(@RequestParam(value = "projectId") Long projectId) {
        return ResponseEntity.ok(this.categoryMapper.toCategoryDTOs(this.projectService.getAllProjectCategoriesOrderedByPosition(projectId)));
    }

    @GetMapping("/projectCategoryOverview/{id}")
    public ResponseEntity<Map<String, List<ProjectCategoryUserOverviewDTO>>> getProjectCategoryOverview(@PathVariable Long id,
                                                                                                    @RequestParam(value = "categoryParent", defaultValue = "") Long categoryParentId) {
        return ResponseEntity.ok(this.projectService.getProjectCategoryOverview(id, categoryParentId));
    }

    @GetMapping("/projectTime/{id}")
    public ResponseEntity<ProjectTimeOverviewDTO> getProjectUsersTimesOverview(
            @PathVariable Long id,
            @RequestParam(value = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(value = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @RequestParam(value = "categoryName", defaultValue = "_all_") String categoryName,
            @RequestParam(value = "user", defaultValue = "") String userUid
            ) {
        ProjectTimeOverviewDTO response = new ProjectTimeOverviewDTO();

        response.setTimeList(this.timeMapper.timeListToTimeDTOListSimple(this.projectService.getProjectUsersTimesOverview(id, dateFrom, dateTo, categoryName, userUid)));
        response.setTimeForThisYear(this.projectService.getProjectTimeForThisYear(id));

        return ResponseEntity.ok(response);
    }
}
