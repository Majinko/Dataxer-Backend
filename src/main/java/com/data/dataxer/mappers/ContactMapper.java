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

    Contact toContact(ContactDTO contactDTO);

    List<ContactDTO> toContactDTOs(List<Contact> contacts);

    List<Contact> toContacts(List<ContactDTO> contactDTOS);
}
