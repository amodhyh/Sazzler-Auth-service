package com.sazzler.ecommerce.sazzler_auth_service.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="roles")
public class Role{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Column(name="ROLE_ID")
    private Long id;

    @Getter @Setter
    @Column(name="ROLE_NAME", unique = true, nullable = false)
    private String name;

    @Getter
    @OneToMany(mappedBy = "role")
    private Set<User> users=new HashSet<>();

    @Getter
    @ManyToMany
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "ROLE_ID"),
            inverseJoinColumns = @JoinColumn(name = "PERMISSION_ID")
    )
    Set<Permission> permissions=new HashSet<>();
}
