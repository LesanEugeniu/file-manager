package md.social.filemanager.service.impl;

import md.social.filemanager.dto.FileDataDto;
import md.social.filemanager.dto.ObjectMapper;
import md.social.filemanager.model.FileData;
import md.social.filemanager.repository.StorageRepository;
import md.social.filemanager.service.StorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class StorageServiceImpl implements StorageService
{
    private final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);
    private final StorageRepository storageRepository;

    @Autowired
    public StorageServiceImpl(StorageRepository storageRepository)
    {
        this.storageRepository = storageRepository;
    }

    @Override
    public Page<FileDataDto> getUserFiles(String userName, Pageable pageable)
    {
        Page<FileData> allFilesPage = storageRepository.findAllByCreatedBy(userName, pageable);
        logger.debug("{} get files [total_elements: {}, page_size: {}]",
                userName, allFilesPage.getTotalElements(), allFilesPage.getSize());
        return ObjectMapper.mapTo(allFilesPage);
    }

    @Override
    public Long saveFile(String userName, MultipartFile file)
    {
        return 0L;
    }

    @Override
    public void deleteFile(String userName, Long fileId)
    {

    }
}
