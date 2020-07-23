package com.data.dataxer.models.domain;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Entity
@Getter
@Setter
public class Document implements Serializable {

    @Id
    private Long documentId;

    @Id
    @OneToOne(mappedBy = "documentPackId")
    DocumentPack documentPack;

    private String documentType;

}
