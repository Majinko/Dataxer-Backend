package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.Periods;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class DocumentNumberGenerator extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(nullable = false)
    private String format;

    @Enumerated(EnumType.STRING)
    private DocumentType type;

    @Enumerated(EnumType.STRING)
    private Periods period;

    private Integer isDefault;

    private String lastNumber;

}
