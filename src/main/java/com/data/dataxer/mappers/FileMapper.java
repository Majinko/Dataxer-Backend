package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.File;
import com.data.dataxer.models.dto.FileDTO;
import org.mapstruct.Mapper;

@Mapper
public interface FileMapper {
    FileDTO fileToFileDTO(File file);
}
