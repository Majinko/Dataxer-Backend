package com.data.dataxer.models.dto;

import com.data.dataxer.models.enums.MailAccountState;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailAccountsDTO {

    private Long id;
    private String hostName;
    private Integer port;
    private String userName;
    private String password;
    private MailAccountState state;

}
