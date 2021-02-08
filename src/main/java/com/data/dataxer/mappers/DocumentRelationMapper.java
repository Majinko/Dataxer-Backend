package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.DocumentRelation;
import com.data.dataxer.models.dto.DocumentRelationDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface DocumentRelationMapper {
    List<DocumentRelationDTO> documentRelationToDocumentRelationDTO(List<DocumentRelation> documentRelation);
}
