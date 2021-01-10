package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.MailAccounts;
import com.data.dataxer.models.dto.MailAccountsDTO;
import org.mapstruct.Mapper;

@Mapper
public interface MailAccountsMapper {

    MailAccounts mailAccountsDTOToMailAccounts(MailAccountsDTO mailAccountsDTO);

    MailAccountsDTO mailAccountsToMailAccountsDTO(MailAccounts mailAccounts);

}
