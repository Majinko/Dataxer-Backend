package com.data.dataxer.controllers;

import com.data.dataxer.mappers.ProjectMapper;
import com.data.dataxer.models.dto.ProjectDTO;
import com.data.dataxer.services.ProjectService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/project")
public class ProjectController {
    private final ProjectService projectService;
    private final ProjectMapper projectMapper;

    public ProjectController(ProjectService projectService, ProjectMapper projectMapper) {
        this.projectService = projectService;
        this.projectMapper = projectMapper;
    }

    @PostMapping("/store")
    public void store(@RequestBody ProjectDTO projectDTO) {
        this.projectService.store(projectMapper.projectDTOtoProject(projectDTO));
    }

    @GetMapping("/paginate")
    public ResponseEntity<Page<ProjectDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(projectService.paginate(pageable).map(projectMapper::projectToProjectDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectMapper.projectToProjectDto(this.projectService.getById(id)));
    }


    @PostMapping("/update")
    public void update(@RequestBody ProjectDTO projectDTO) {
        this.projectService.update(projectMapper.projectDTOtoProject(projectDTO));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.projectService.destroy(id);
    }
}
