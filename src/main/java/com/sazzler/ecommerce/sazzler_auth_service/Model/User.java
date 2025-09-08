package com.sazzler.ecommerce.sazzler_auth_service.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter @Setter
@Table(name="USER")
public class User {

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @SequenceGenerator(name = "id_seq", sequenceName = "USER_ID_SEQ", allocationSize = 1)
    @Column(name = "USER_ID")
    int userId;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "DATE_OF_BIRTH")
    private   Date dob;

    @Column(name = "CREATED_AT")
    private Date createdAt;

    @Column(name = "PASSWORD")
    private String password;

    @Id
    @Column(name = "USER_NAME", unique = true)
    private String username;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID")
    Role role;




}

