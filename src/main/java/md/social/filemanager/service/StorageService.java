package md.social.filemanager.service;

import md.social.filemanager.dto.FileDataDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService
{
    Page<FileDataDto> getFiles(String userName, Pageable pageable);

    FileDataDto getFileById(String userName, Long id);

    FileDataDto saveFile(String userName, MultipartFile file);

    void deleteFile(String userName, Long fileId);
}
