package md.social.filemanager.controller;

import md.social.filemanager.dto.FileDataDto;
import md.social.filemanager.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController
{
    private final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<Page<FileDataDto>> getFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal)
    {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(storageService.getFiles(principal.getName(), pageable));
    }

    @PostMapping
    public ResponseEntity<FileDataDto> saveFile(
            @RequestParam("file") MultipartFile file,
            Principal principal)
    {
        return ResponseEntity.ok(storageService.saveFile(principal.getName(), file));
    }
}
