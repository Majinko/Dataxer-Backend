package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.DocumentNumberGenerator;
import com.data.dataxer.models.dto.DocumentNumberGeneratorDTO;
import org.mapstruct.Mapper;

@Mapper
public interface DocumentNumberGeneratorMapper {

    DocumentNumberGenerator documentNumberGeneratorDTOToDocumentNumberGenerator(DocumentNumberGeneratorDTO documentNumberGeneratorDTO);

    DocumentNumberGeneratorDTO documentNumberGeneratorToDocumentNumberGeneratorDTO(DocumentNumberGenerator documentNumberGenerator);

    DocumentNumberGeneratorDTO documentNumberGeneratorToDocumentNumberGeneratorDTOSimple(DocumentNumberGenerator documentNumberGenerator);

}
