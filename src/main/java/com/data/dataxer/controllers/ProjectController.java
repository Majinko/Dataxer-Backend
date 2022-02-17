package com.data.dataxer.controllers;

import com.data.dataxer.mappers.CategoryMapper;
import com.data.dataxer.mappers.ProjectMapper;
import com.data.dataxer.mappers.TimeMapper;
import com.data.dataxer.mappers.UserMapper;
import com.data.dataxer.models.dto.*;
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

@RestController
@RequestMapping("/api/project")
@PreAuthorize("hasPermission(null, 'Project', 'Project')")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final CategoryMapper categoryMapper;
    private final TimeMapper timeMapper;
    private final UserMapper userMapper;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper, CategoryMapper categoryMapper, TimeMapper timeMapper, UserMapper userMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
        this.categoryMapper = categoryMapper;
        this.timeMapper = timeMapper;
        this.userMapper = userMapper;
    }

    @GetMapping("/paginate")
    public ResponseEntity<Page<ProjectDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(-project.number)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("numberew")));

        return ResponseEntity.ok(projectService.paginate(pageable, rqlFilter, sortExpression).map(projectMapper::projectToProjectDTOWithoutCategory));
    }

    @GetMapping("/getById/{id}")
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


    @PostMapping("/store")
    public ResponseEntity<ProjectDTO> store(@RequestBody ProjectDTO projectDTO) {
        return ResponseEntity.ok(projectMapper.projectToProjectDTO(this.projectService.store(projectMapper.projectDTOtoProject(projectDTO))));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProjectDTO>> all() {
        return ResponseEntity.ok(projectMapper.projectToProjectDTOs(this.projectService.all()));
    }

    @GetMapping("/allByClient")
    public ResponseEntity<List<ProjectDTO>> allByClient(
            @RequestParam(value = "clientId") Long clientId
    ) {
        return ResponseEntity.ok(projectMapper.projectToProjectDTOs(this.projectService.allByClient(clientId)));
    }

    @GetMapping("/allHasCost")
    public ResponseEntity<List<ProjectDTO>> allHasCost() {
        return ResponseEntity.ok(projectMapper.projectToProjectDTOs(this.projectService.allHasCost()));
    }

    @GetMapping("/allHasInvoice")
    public ResponseEntity<List<ProjectDTO>> allHasInvoice() {
        return ResponseEntity.ok(projectMapper.projectToProjectDTOs(this.projectService.allHasInvoice()));
    }

    @GetMapping("/allHasPriceOffer")
    public ResponseEntity<List<ProjectDTO>> allHasPriceOffer() {
        return ResponseEntity.ok(projectMapper.projectToProjectDTOs(this.projectService.allHasPriceOffer()));
    }

    @GetMapping("/allHasPriceOfferCostInvoice")
    public ResponseEntity<List<ProjectDTO>> allHasPriceOfferCostInvoice() {
        return ResponseEntity.ok(projectMapper.projectToProjectDTOs(this.projectService.allHasPriceOfferCostInvoice()));
    }

    @GetMapping("/allHasUserTime")
    public ResponseEntity<List<ProjectDTO>> allHasUserTime() {
        return ResponseEntity.ok(projectMapper.projectToProjectDTOs(this.projectService.allHasUserTime()));
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
    public ResponseEntity<List<ProjectTimePriceOverviewCategoryDTO>> getProjectCategoryOverview(
            @PathVariable Long id,
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

    @GetMapping("/projectManHours/{id}")
    public ResponseEntity<ProjectManHoursDTO> getProjectManHours(
            @PathVariable Long id,
            @RequestParam(value = "companyIds", required = false) List<Long> companyIds
    ) {
        return ResponseEntity.ok(this.projectService.getProjectManHours(id));
    }

    //todo remove
    @GetMapping("/prepareEvaluation/{id}")
    public ResponseEntity<EvaluationPreparationDTO> projectEvaluationPreparation(@PathVariable Long id) {
        return  ResponseEntity.ok(this.projectService.evaluationPreparationProjectData(id));
    }

    @PostMapping("/addProfitUser/{id}")
    public void addProfitUser(@PathVariable Long id, @RequestBody AppUserDTO appUserDTO) {
        this.projectService.addProfitUser(id, this.userMapper.appUserDTOtoAppUser(appUserDTO));
    }

    @PostMapping("/removeProfitUser/{id}")
    public void removeProfitUser(@PathVariable Long id, @RequestBody AppUserDTO appUserDTO) {
        this.projectService.removeProfitUser(id, this.userMapper.appUserDTOtoAppUser(appUserDTO));
    }
}
