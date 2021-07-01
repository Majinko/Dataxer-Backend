package com.data.dataxer.controllers;

import com.data.dataxer.mappers.StorageMapper;
import com.data.dataxer.mappers.TaskMapper;
import com.data.dataxer.models.dto.TaskDTO;
import com.data.dataxer.models.dto.UploadContextDTO;
import com.data.dataxer.services.TaskService;
import com.data.dataxer.services.storage.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/task")
@PreAuthorize("hasPermission(null, 'Task', 'Task')")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @Autowired
    private StorageService storageService;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private StorageMapper storageMapper;

    @PostMapping("/store")
    public void store(@RequestBody UploadContextDTO<TaskDTO> uploadContext) {
        TaskDTO task = taskMapper.taskToTaskDTO(this.taskService.store(taskMapper.taskDTOtoTask(uploadContext.getObject())));

        if (!uploadContext.getFiles().isEmpty()) {
            uploadContext.getFiles().forEach(file -> {
                this.storageService.store(storageMapper.storageFileDTOtoStorage(file), task.getId(), "task");
            });
        }

        if (uploadContext.getObject().isSendEmail()) {
            //todo
        }
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

        return ResponseEntity.ok(taskService.paginate(pageable, rqlFilter, sortExpression).map(taskMapper::taskToTaskDTOSimple));
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
