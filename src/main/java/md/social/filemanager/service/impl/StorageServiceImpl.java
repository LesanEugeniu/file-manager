package md.social.filemanager.service.impl;

import md.social.filemanager.dto.FileDataDto;
import md.social.filemanager.dto.ObjectMapper;
import md.social.filemanager.exception.FileManagerBaseException;
import md.social.filemanager.model.FileData;
import md.social.filemanager.repository.StorageRepository;
import md.social.filemanager.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService
{
    private final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    private final StorageRepository storageRepository;

    private final String FOLDER_PATH;

    @Autowired
    public StorageServiceImpl(StorageRepository storageRepository,
                              @Value("${base.folder.path}") String folderPath)
    {
        this.storageRepository = storageRepository;
        FOLDER_PATH = folderPath;
    }

    @Override
    public Page<FileDataDto> getFiles(String userName, Pageable pageable)
    {
        Page<FileData> allFilesPage = storageRepository.findAllByCreatedBy(userName, pageable);
        logger.debug("{} get files [total_elements: {}, page_size: {}]",
                userName, allFilesPage.getTotalElements(), allFilesPage.getSize());
        return ObjectMapper.mapTo(allFilesPage);
    }

    @Override
    public FileDataDto getFileById(String userName, Long id) {
        return null;
    }

    @Override
    public FileDataDto saveFile(String userName, MultipartFile file)
    {
        return uploadImageToFileSystem(userName, file);
    }

    @Override
    public void deleteFile(String userName, Long fileId)
    {

    }

    private FileDataDto uploadImageToFileSystem(String userName, MultipartFile file)
    {
        String newFileName = UUID.randomUUID() + file.getContentType();
        String filePath = FOLDER_PATH + "/" + userName + "/" + newFileName;

        FileData fileData = new FileData();
        fileData.setType(file.getContentType());
        fileData.setFilePath(filePath);

        fileData = storageRepository.save(fileData);

        try {
            file.transferTo(new File(filePath));
        } catch (IOException e){
            logger.error("Error moving file {}", filePath);
            throw new FileManagerBaseException("Erorr with file management",
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        if (fileData != null) {
            logger.info("file uploaded successfully : {}", filePath);
        }
        return ObjectMapper.mapTo(fileData);
    }
}
