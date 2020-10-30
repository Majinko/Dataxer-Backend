package com.data.dataxer.mappers;

import com.data.dataxer.models.domain.BankAccount;
import com.data.dataxer.models.dto.BankAccountDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface BankAccountMapper {
    BankAccountDTO bankAccountToBankAccountDTO(BankAccount bankAccountDTO);

    BankAccount bankAccountDTOtoBankAccount(BankAccountDTO bankAccount);

    List<BankAccountDTO> bankAccountsToBankAccountDTOs(List<BankAccount> bankAccounts);
}
