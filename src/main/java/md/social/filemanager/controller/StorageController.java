package md.social.filemanager.controller;

import md.social.filemanager.dto.FileDataDto;
import md.social.filemanager.service.impl.StorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/storage")
public class StorageController
{
    private final StorageServiceImpl storageService;

    @Autowired
    public StorageController(StorageServiceImpl storageService) {
        this.storageService = storageService;
    }

    @GetMapping
    public ResponseEntity<Page<FileDataDto>> getUserFiles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal)
    {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(storageService.getUserFiles(principal.getName(), pageable));
    }

}
