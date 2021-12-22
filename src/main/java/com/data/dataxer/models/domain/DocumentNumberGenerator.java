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

    private DocumentType type;

    private Periods period;

    private Boolean isDefault;

    private String lastNumber;

    public DocumentNumberGenerator() {}

    public DocumentNumberGenerator(
            String title,
            String format,
            DocumentType type,
            Periods period,
            boolean isDefault,
            String lastNumber) {
        this.title = title;
        this.format = format;
        this.type = type;
        this.period = period;
        this.isDefault = isDefault;
        this.lastNumber = lastNumber;
    }
}
