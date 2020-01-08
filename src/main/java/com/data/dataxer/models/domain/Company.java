package com.data.dataxer.models.domain;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 50)
    private String name;

    @ManyToMany
    List<DataxerUser> users = new ArrayList<DataxerUser>();

    @OneToMany(mappedBy="company")
    private List<BillingInformation> billingInformation = new ArrayList<BillingInformation>();
}
