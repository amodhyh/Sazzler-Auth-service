package com.sazzler.ecommerce.sazzler_auth_service.Model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.Date;

@Entity
@Getter @Setter
@Table(name="PERSON")
public class User {

    @Column(name = "USER_ID")
    int userId;

    @Column(name = "EMAIL", unique = true)
    String email;

    @Column(name = "DATE_OF_BIRTH")
    Date dob;

    @Column(name = "CREATED_AT")
    Date createdAt;

    @Column(name = "PASSWORD")
    String password;

    @Id
    @Column(name = "USER_NAME", unique = true)
    String username;

    @Column(name = "FIRST_NAME")
    String firstName;

    @Column(name = "LAST_NAME")
    String lastName;

}

