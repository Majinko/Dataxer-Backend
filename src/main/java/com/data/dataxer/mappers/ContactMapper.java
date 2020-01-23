package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.Contact;
import com.data.dataxer.models.dto.ContactDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ContactMapper {
    ContactMapper INSTANCE = Mappers.getMapper(ContactMapper.class);

    ContactDTO toContactDto(Contact contact);

    @Mapping(target = "updatedAt", source = "")
    @Mapping(target = "updated", source = "")
    @Mapping(target = "projects", source = "")
    @Mapping(target = "note", source = "")
    @Mapping(target = "deletedAt", source = "")
    @Mapping(target = "createdAt", source = "")
    @Mapping(target = "created", source = "")
    @Mapping(target = "company", source = "")
    Contact toContact(ContactDTO contactDTO);

    List<ContactDTO> toContactDTOs(List<Contact> contacts);

    List<Contact> toContacts(List<ContactDTO> contactDTOS);
}
