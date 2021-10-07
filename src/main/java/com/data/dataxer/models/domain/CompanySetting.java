package com.data.dataxer.models.domain;

import com.data.dataxer.mappers.HashMapConverter;
import com.data.dataxer.models.enums.CompanySettingType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Map;

@Getter
@Setter
@Entity
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE company_setting SET deleted_at = now() WHERE id = ?")
public class CompanySetting extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private CompanySettingType companySettingType;

    @Column(columnDefinition = "text")
    @Convert(converter = HashMapConverter.class)
    protected Map<String, Object> data;
}
