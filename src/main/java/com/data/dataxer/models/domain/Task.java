package com.data.dataxer.models.domain;

import com.data.dataxer.models.enums.DocumentState;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@FilterDef(
        name = "companyCondition",
        parameters = @ParamDef(name = "companyId", type = "long")
)
@Filter(
        name = "companyCondition",
        condition = "company_id = :companyId"
)
public class Task extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "project_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Project project;

    @JoinColumn(name = "catergory_id", referencedColumnName = "id")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Category category;

    @JoinColumn(name = "user_uid", referencedColumnName = "uid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser user;

    @JoinColumn(name = "user_from_uid", referencedColumnName = "uid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private AppUser userFrom;

    @JoinTable(
            name = "storage",
            joinColumns = @JoinColumn(name = "fileAbleId"),
            inverseJoinColumns = @JoinColumn(name = "id"),
            foreignKey = @javax.persistence.ForeignKey(name = "none")
    )
    @Where(clause="file_able_type='storage'")
    @OneToMany(fetch = FetchType.LAZY)
    private List<Storage> files = new ArrayList<>();

    private String title;

    @Column(columnDefinition = "text")
    private String description;

    private String completion;

    DocumentState state;

    private LocalDateTime finishedAt;
}
