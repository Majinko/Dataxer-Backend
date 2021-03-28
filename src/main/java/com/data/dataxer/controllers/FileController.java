package com.data.dataxer.controllers;

import com.data.dataxer.mappers.FileMapper;
import com.data.dataxer.models.domain.File;
import com.data.dataxer.models.dto.FileDTO;
import com.data.dataxer.services.FileService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/file")
@PreAuthorize("hasPermission(null, 'File', 'File')")
public class FileController {
    private final FileService fileService;
    private final FileMapper fileMapper;

    public FileController(FileService fileService, FileMapper fileMapper) {
        this.fileService = fileService;
        this.fileMapper = fileMapper;
    }

    @PostMapping("/uploadFile")
    public ResponseEntity<FileDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "true") Boolean isDefault
    ) {
        File storedFile = this.fileService.storeFile(file, isDefault);

        return ResponseEntity.ok(this.fileMapper.fileToFileDTO(storedFile));
    }

    @PostMapping("/uploadMultipleFiles")
    public ResponseEntity<List<FileDTO>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("companyId") Long companyId
    ) {
        return ResponseEntity.ok(
                Arrays.stream(files)
                        .map(file -> uploadFile(file, false).getBody())
                        .collect(Collectors.toList()));
    }

    @GetMapping("/downloadFile/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        // Load file as Resource
        Resource resource = this.fileService.loadFileAsResource(fileName);

        // Try to determine file's content type
        String contentType;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("File " + fileName + " not found!");
        }

        // Fallback to the default content type if type could not be determined
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @ResponseBody
    @GetMapping("/show/{fileName:.+}")
    public Resource getFile(@PathVariable String fileName) {
        return this.fileService.loadFileAsResource(fileName);
    }

    @GetMapping("/getFileByName/{fileName}")
    public ResponseEntity<FileDTO> getFileByName(@PathVariable String fileName) {
        return ResponseEntity.ok(
                this.fileMapper.fileToFileDTO(this.fileService.getFileByName(fileName))
        );
    }

    @RequestMapping(value = "/paginate", method = RequestMethod.GET)
    public ResponseEntity<Page<FileDTO>> paginate(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "filters", defaultValue = "") String rqlFilter,
            @RequestParam(value = "sortExpression", defaultValue = "sort(+file.id)") String sortExpression
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("id")));

        return ResponseEntity.ok(this.fileService.paginate(pageable, rqlFilter, sortExpression).map(this.fileMapper::fileToFileDTO));
    }
}
