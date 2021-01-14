package com.data.dataxer.controllers;

import com.data.dataxer.mappers.TaskMapper;
import com.data.dataxer.models.dto.TaskDTO;
import com.data.dataxer.services.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;
    private final TaskMapper taskMapper;

    public TaskController(TaskService taskService, TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @PostMapping("/store")
    public void store(@RequestBody TaskDTO taskDTO) {
        this.taskService.store(taskMapper.taskDTOtoTask(taskDTO));
    }

    @PostMapping("/update")
    public void update(@RequestBody TaskDTO taskDTO) {
        this.taskService.update(taskMapper.taskDTOtoTask(taskDTO));
    }

    @GetMapping("/paginate")
    public ResponseEntity<Page<TaskDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+task.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(taskService.paginate(pageable, rqlFilter, sortExpression).map(taskMapper::taskToTaskDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taskMapper.taskToTaskDTO(taskService.getById(id)));
    }

    @GetMapping("/destroy/{id}")
    public void destroy(@PathVariable Long id) {
        this.taskService.destroy(id);
    }
}
