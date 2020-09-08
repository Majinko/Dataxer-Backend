package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;

@Entity
@Setter
@Getter
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
