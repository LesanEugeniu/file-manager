package md.social.filemanager.service.impl;

import jakarta.transaction.Transactional;
import md.social.filemanager.dto.FileDataDto;
import md.social.filemanager.dto.NotificationType;
import md.social.filemanager.dto.ObjectMapper;
import md.social.filemanager.dto.kafka.UserNotification;
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
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class StorageServiceImpl implements StorageService
{
    private final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);

    private final StorageRepository storageRepository;

    private final String FOLDER_PATH;

    private final KafkaProducer kafkaProducer;

    @Autowired
    public StorageServiceImpl(StorageRepository storageRepository,
                              @Value("${base.folder.path}") String folderPath,
                              KafkaProducer kafkaProducer)
    {
        this.storageRepository = storageRepository;
        FOLDER_PATH = folderPath;
        this.kafkaProducer = kafkaProducer;
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
    public FileDataDto getFileById(String userName, Long id)
    {
        Optional<FileData> fileData = storageRepository.findByIdAndCreatedBy(id, userName);
        if (fileData.isEmpty()){
            throw new FileManagerBaseException(String.format("File with id [ %s ] doesn't exist", id),
                    HttpStatus.BAD_REQUEST.name(), HttpStatus.BAD_REQUEST.value());
        }
        return ObjectMapper.mapTo(fileData.get());
    }

    @Transactional
    @Override
    public FileDataDto saveFile(String userName, MultipartFile file)
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
            buildNotification(userName, NotificationType.FILE_UPLOAD_FAILURE,
                    String.format("File with id: %s failed to upload", fileData.getId()));
            throw new FileManagerBaseException("Error with file management",
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        if (fileData != null) {
            logger.info("file uploaded successfully : {}", filePath);
            buildNotification(userName, NotificationType.FILE_UPLOAD_SUCCESS,
                    String.format("File with id: %s successfully uploaded", fileData.getId()));
        }
        return ObjectMapper.mapTo(fileData);
    }

    @Override
    public void deleteFile(String userName, Long fileId)
    {
        FileDataDto file = getFileById(userName, fileId);
        String filePath = file.getFilePath();
        File systemFile = new File(filePath);
        if (!systemFile.delete()){
            buildNotification(userName, NotificationType.FILE_DELETION_FAILURE,
                    String.format("File with id: %s failed on delete", file.getId()));
            logger.error("Error deleting file {}", file);
            throw new FileManagerBaseException(
                    String.format("Error deleting file with id [%s]", file.getId()),
                    HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR.value()
            );
        }
        storageRepository.deleteById(file.getId());
        buildNotification(userName, NotificationType.FILE_DELETION_FAILURE,
                String.format("File with id: %s failed on delete", file.getId()));
    }

    @Override
    public byte[] downloadFile(String userName, Long fileId)
    {
        FileDataDto file = getFileById(userName, fileId);
        try {
            byte[] bytes = Files.readAllBytes(new File(file.getFilePath()).toPath());
            buildNotification(userName, NotificationType.FILE_DOWNLOAD_SUCCESS,
                    String.format("File with id: %s successfully downloaded", file.getId()));
            return bytes;
        } catch (IOException e) {
            logger.error("Error on file bytes load {}", String.valueOf(e));
            buildNotification(userName, NotificationType.FILE_DOWNLOAD_FAILURE,
                    String.format("File with id: %s failed on download", file.getId()));
            throw new FileManagerBaseException("Error on file download",
                    HttpStatus.INTERNAL_SERVER_ERROR.name(),
                    HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    private void buildNotification(String userName, NotificationType type, String message)
    {
        UserNotification notification = new UserNotification();
        notification.setUserName(userName);
        notification.setNotificationType(type);
        notification.setTimestamp(LocalDateTime.now());
        notification.setMessage(message);
        kafkaProducer.sendMessage(notification);
    }
}
