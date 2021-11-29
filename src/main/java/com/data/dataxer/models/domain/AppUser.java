package com.data.dataxer.models.domain;

import com.data.dataxer.security.model.Role;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "app_user")
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE category SET deleted_at = now() WHERE id = ?")
public class AppUser implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false, length = 100)
    private String uid;

    private String firstName;

    private String lastName;

    @Column(unique = true, nullable = false, updatable = false, length = 50)
    private String email;

    private String phone;

    private String street;

    private String city;

    private String postalCode;

    private String country;

    @Column(name = "connected")
    private Boolean connected = false;

    @OneToOne(fetch = FetchType.LAZY)
    private Company defaultCompany;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<Role> roles = new ArrayList<>();

    @Transient
    @ManyToMany(fetch = FetchType.LAZY)
    private List<Company> companies = new ArrayList<>();

    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column
    private LocalDateTime deletedAt;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AppUser other = (AppUser) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
