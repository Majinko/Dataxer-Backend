package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Storage;
import com.data.dataxer.models.dto.StorageFileDTO;
import org.mapstruct.Mapper;

@Mapper
public interface StorageMapper {
    StorageFileDTO storageToStorageFileDTO(Storage storageFileDTO);

    Storage storageFileDTOtoStorage(StorageFileDTO storage);
}
