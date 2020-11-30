package com.data.dataxer.models.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UploadContextDTO<T> {
    private List<StorageFileDTO> files;
    private T object;
}
