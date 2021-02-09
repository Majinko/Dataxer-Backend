package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.DocumentBase;
import com.data.dataxer.models.domain.DocumentRelation;
import com.data.dataxer.models.dto.DocumentRelationDTO;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper
public interface DocumentRelationMapper {
    DocumentRelationDTO documentToDocumentRelationDTO(DocumentRelation documentRelation);

    @Named(value = "documentBaseToDocumentRelationDTO")
    @Mapping(target = "relatedDocumentId", source = "id")
    @Mapping(target = "documentTitle", source = "title")
    DocumentRelationDTO documentBaseToDocumentRelationDTO(DocumentBase documentBase);

    @IterableMapping(qualifiedByName = "documentBaseToDocumentRelationDTO")
    List<DocumentRelationDTO> documentsBaseToDocumentRelationDTOs(List<DocumentBase> documentBases);
}
