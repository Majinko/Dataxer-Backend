package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.MailAccountState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE mail_accounts SET deleted_at = now() WHERE id = ?")
public class MailAccounts extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String hostName;

    @Column(nullable = false)
    private Integer port;

    @Column(nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private MailAccountState state;

    private LocalDateTime deletedAt;

}
