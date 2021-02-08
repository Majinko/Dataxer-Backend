package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.persistence.Entity;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

@Entity
@Setter
@Getter
@FilterDef(
        name = "companyCondition",
        parameters = @ParamDef(name = "companyId", type = "long")
)
@Filter(
        name = "companyCondition",
        condition = "company_id = :companyId"
)
public class File extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String extension;

    private Integer fileHash;

    private BigDecimal size;

    private String downloadURL;

    private String showURL;

    private Boolean isDefault;

    public File() {

    }

    public File(MultipartFile file, Boolean isDefault, String downloadURL, String showURL) {
        this.name = file.getOriginalFilename();
        this.extension = file.getContentType();
        this.size = BigDecimal.valueOf(file.getSize());
        this.isDefault = isDefault;
        this.fileHash = file.getResource().hashCode();
        this.downloadURL = downloadURL;
        this.showURL = showURL;
    }
}
