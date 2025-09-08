package com.sazzler.ecommerce.sazzler_auth_service.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "PERMISSION")
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="PERMISSION_ID")
    private Long id;

    @Column(name="PERMISSION_NAME", unique = true, nullable = false)
    @Getter @Setter
    private String permissionName;

    @Getter
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles;


    }
