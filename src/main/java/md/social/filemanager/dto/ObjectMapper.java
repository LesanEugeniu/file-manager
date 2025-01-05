package md.social.filemanager.dto;

import md.social.filemanager.model.FileData;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

public class ObjectMapper
{
    private static final ModelMapper modelMapper = new ModelMapper();

    public static FileDataDto mapTo(FileData fileData)
    {
        return modelMapper.map(fileData, FileDataDto.class);
    }

    public static Page<FileDataDto> mapTo(Page<FileData> fileDataPage)
    {
        List<FileDataDto> filesDto = fileDataPage.getContent().stream()
                .map(ObjectMapper::mapTo)
                .toList();
        PageRequest pageRequest = PageRequest.of(fileDataPage.getNumber(), fileDataPage.getSize());
        return new PageImpl<>(filesDto, pageRequest, fileDataPage.getTotalElements());
    }
}
